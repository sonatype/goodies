/*
 * Copyright (c) 2007-2013 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package org.sonatype.sisu.litmus.testsupport.hamcrest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

/**
 * Java beans related matchers.
 *
 * @since 1.0
 */
public class BeanMatchers
{

  /**
   * Are the two java beans similar?.
   * The matching is done by doing a deep equals of all getters present in actual and expected bean.
   * If bean contains array/collections/iterables/iterator getters it will apply similarity check on elements of
   * those.
   *
   * @param expected expected value to be matched
   * @param <T>      expected type
   * @return true if beans are similar
   */
  @Factory
  public static <T> Matcher<T> similarTo(final T expected) {
    return new SimilarMatcher<T>(expected);
  }

  /**
   * Are the two java beans similar?.
   * The matching is done by doing a deep equals of all getters present in actual and expected bean.
   * If bean contains array/collections/iterables/iterator getters it will apply similarity check on elements of
   * those.
   *
   * @param <T> expected type
   * @since 1.0
   */
  public static class SimilarMatcher<T>
      extends BaseMatcher<T>
  {

    /**
     * Expected value. Can be null.
     */
    private final T expected;

    private SimilarMatcher<?> parent;

    private SimilarMatcher<?> child;

    /**
     * What is checking.
     */
    private final String path;

    /**
     * Matcher that did not match. Used to report the failure. Null in case that actual/expected matches.
     */
    private Matcher failingMatcher;

    /**
     * Value that actually failed.
     */
    public Object failingActual;

    /**
     * Description of failing check. (only used for debugging)
     */
    private String failingCheck;

    /**
     * Constructor.
     *
     * @param expected expected value. Can be null.
     * @since 1.0
     */
    public SimilarMatcher(final T expected) {
      this.expected = expected;
      this.path = "";
    }

    /**
     * Constructor.
     *
     * @param expected expected value. Can be null.
     * @param parent   parent similarity matcher
     */
    private SimilarMatcher(final T expected, final String path, final SimilarMatcher<?> parent) {
      this.expected = expected;
      this.parent = parent;
      this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Object actual) {
      // match null values
      if (expected == null || actual == null) {
        return match(actual, equalTo(expected), "Null check");
      }
      // match iterable elements
      if (expected instanceof Iterable) {
        return matchIterable(actual, expected);
      }
      // match iterator elements
      if (expected instanceof Iterator) {
        return matchIterator(actual, expected);
      }
      // match array elements
      if (expected.getClass().isArray()) {
        return matchArray(actual, expected);
      }
      // match map elements
      if (expected instanceof Map) {
        return matchMap(actual, expected);
      }
      // maybe the values are equal, so we do not have to do any check
      if (match(actual, equalTo(expected), "Maybe values are equal")) {
        return true;
      }

      try {
        if (!isBean(expected)) {
          return false;
        }
        final Collection<Method> getters = commonGetters(expected, actual);
        if (getters.isEmpty()) {
          return false;
        }
        // match each common getters between expected and actual
        for (final Method m : getters) {
          Method em = null;
          try {
            em = expected.getClass().getMethod(m.getName());
          }
          catch (NoSuchMethodException e) {
            if (!match(expected, isA(m.getDeclaringClass()), "Expected has method")) {
              return false;
            }
          }

          Method am = null;
          try {
            am = actual.getClass().getMethod(m.getName());
          }
          catch (NoSuchMethodException e) {
            if (!match(actual, isA(m.getDeclaringClass()), "Actual has method")) {
              return false;
            }
          }

          final Object expectedValue = em.invoke(expected);
          final Object actualValue = am.invoke(actual);

          if (!match(actualValue, new SimilarMatcher<Object>(expectedValue, "." + m.getName() + "()", this),
              "Getters are similar")) {
            return false;
          }
        }
      }
      catch (Exception e) {
        Throwables.propagate(e);
      }

      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeTo(Description description) {
      if (path != null) {
        description.appendText(path);
      }
      if (child != null) {
        child.describeTo(description);
      }
      else if (failingMatcher != null) {
        description.appendText(" ");
        failingMatcher.describeTo(description);
      }
      else {
        description.appendValue(expected);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeMismatch(Object actual, Description description) {
      if (path != null) {
        description.appendText(path);
      }
      if (child != null) {
        child.describeMismatch(failingActual, description);
      }
      else if (failingMatcher != null) {
        description.appendText(" ");
        failingMatcher.describeMismatch(this.failingActual, description);
      }
      else {
        description.appendValue(failingActual);
      }
    }

    /**
     * Determine all common methods between expected and actual
     *
     * @param expected expected value
     * @param actual   actual value
     * @return common methods by name. Empty if there are no common methods
     */
    private Collection<Method> commonGetters(final Object expected, final Object actual) {
      if (expected == null || actual == null) {
        return Collections.emptyList();
      }

      final ArrayList<Class<?>> classes = Lists.newArrayList();
      // find all common classes in hierarchy
      for (final Class<?> clazz : getHierarchy(expected.getClass())) {
        if (clazz.isInstance(actual)) {
          classes.add(clazz);
        }
      }
      // find all common methods
      for (final Class<?> clazz : getInterfaces(expected.getClass())) {
        if (clazz.isInstance(actual)) {
          classes.add(clazz);
        }
      }
      final Set<Method> methods = Sets.newHashSet();
      for (final Class<?> clazz : classes) {
        final Method[] clazzMethods = clazz.getMethods();
        if (clazzMethods != null) {
          for (final Method m : clazzMethods) {
            if (m.getParameterTypes().length == 0
                && !"getClass".equals(m.getName())
                && (m.getName().startsWith("get") || m.getName().startsWith("is"))) {
              methods.add(m);
            }
          }
        }
      }
      return methods;
    }

    /**
     * Gather hierarchy of interfaces for a class.
     *
     * @param clazz to gather interfaces for
     * @return all interfaces of specified class, empty if no interface involved
     */
    private Collection<Class<?>> getInterfaces(final Class<?> clazz) {
      final ArrayList<Class<?>> classes = Lists.newArrayList();
      if (clazz != null) {
        for (final Class<?> iface : clazz.getInterfaces()) {
          classes.add(iface);
          classes.addAll(getInterfaces(iface));
        }
      }
      return classes;
    }

    /**
     * Gather hierarchy of classes (without root Object class).
     *
     * @param clazz to gather hierarchy for
     * @return class hierarchy including specified class
     */
    private Collection<Class<?>> getHierarchy(final Class<?> clazz) {
      final ArrayList<Class<?>> classes = Lists.newArrayList();
      if (clazz != null && !Object.class.equals(clazz)) {
        classes.add(clazz);
        classes.addAll(getHierarchy(clazz.getSuperclass()));
      }
      return classes;
    }

    /**
     * Matches specified value with provided matcher, assigning the failing matcher in case that value does not
     * match.
     *
     * @param actual  value to be matched. Can be null.
     * @param matcher matcher to match value
     * @param where   description of failing check. (only used for debugging)
     * @return true, if value matches
     */
    private boolean match(@Nullable final Object actual, final Matcher<?> matcher, final String where) {
      if (!matcher.matches(actual)) {
        if (parent != null) {
          parent.child = this;
        }
        failingMatcher = matcher;
        failingActual = actual;
        failingCheck = where;
        return false;
      }
      return true;
    }

    /**
     * Matches elements of two iterables.
     *
     * @param actualValue   actual values iterable
     * @param expectedValue expected values iterable
     * @return true, if value matches
     */
    private boolean matchIterable(final Object actualValue, final Object expectedValue) {
      if (!(expectedValue instanceof Iterable)) {
        return true;
      }
      return match(actualValue, isA(Iterable.class), "Is an iterable")
          && matchIterator(((Iterable) actualValue).iterator(), ((Iterable) expectedValue).iterator());
    }

    /**
     * Matches elements of two arrays.
     *
     * @param actualValue   actual values array
     * @param expectedValue expected values array
     * @return true, if value matches
     */
    private boolean matchArray(final Object actualValue, final Object expectedValue) {
      if (!expectedValue.getClass().isArray()) {
        return true;
      }
      if (match(actualValue.getClass().isArray(), is(true), "Is an array")) {
        final Object[] expectedArray = (Object[]) expectedValue;
        final Object[] actualArray = (Object[]) actualValue;
        final SimilarMatcher<? super Object>[] itemsMatchers = new SimilarMatcher[expectedArray.length];
        for (int i = 0; i < expectedArray.length; i++) {
          itemsMatchers[i] = new SimilarMatcher<Object>(expectedArray[i], "[" + i + "]", this);
        }

        return match(actualArray, arrayContaining(itemsMatchers), "Array contains in any order");
      }
      return false;
    }

    /**
     * Matches elements of two iterators.
     *
     * @param actualValue   actual values iterator
     * @param expectedValue expected values iterator
     * @return true, if value matches
     */
    private boolean matchIterator(final Object actualValue, final Object expectedValue) {
      if (!(expectedValue instanceof Iterator)) {
        return true;
      }
      if (match(actualValue, isA(Iterator.class), "Is an iterator")) {
        final List<?> expectedList = Lists.newArrayList((Iterator<?>) expectedValue);
        final List<?> actualList = Lists.newArrayList((Iterator<?>) actualValue);
        final SimilarMatcher<? super Object>[] itemsMatchers = new SimilarMatcher[expectedList.size()];
        int i = 0;
        for (final Object expectedElement : expectedList) {
          itemsMatchers[i] = new SimilarMatcher<Object>(expectedElement, null, this);
          i++;
        }
        return match(actualList, containsInAnyOrder(itemsMatchers), "Iterable contains in any order");
      }
      return false;
    }

    /**
     * Matches elements of two maps.
     *
     * @param actualValue   actual values iterator
     * @param expectedValue expected values iterator
     * @return true, if value matches
     */
    private boolean matchMap(final Object actualValue, final Object expectedValue) {
      if (!(expectedValue instanceof Map)) {
        return true;
      }
      if (match(actualValue, isA(Map.class), "Is a map")) {
        final Map<Object, Object> actualMap = Maps.newHashMap((Map<?, ?>) actualValue);
        for (Map.Entry<?, ?> expectedElement : ((Map<?, ?>) expectedValue).entrySet()) {
          if (!match(actualValue,
              hasEntry(
                  new SimilarMatcher<Object>(expectedElement.getKey(), null, this),
                  new SimilarMatcher<Object>(expectedElement.getValue(), null, this)
              ),
              "Actual map has item")) {
            return false;
          }
          actualMap.remove(expectedElement.getKey());
        }
        if (!actualMap.isEmpty()) {
          Map.Entry<Object, Object> firstExtra = actualMap.entrySet().iterator().next();
          return match(expected, hasKey(firstExtra.getKey()), "Actual map has more elements");
        }
        return true;
      }
      return false;
    }

    /**
     * Whether or not a value is a java bean.
     *
     * @param value to check if is a java bean
     * @return true, if value is not a class that starts with "java.lang"
     */
    private boolean isBean(final Object value) {
      return !value.getClass().getName().startsWith("java.lang");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return path;
    }


  }

}
