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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.BeanMatchers.similarTo;

/**
 * {@link BeanMatchers} tests.
 *
 * @since 1.0
 */
public class BeanMatchersTest
    extends TestSupport
{

  @Test
  public void emptyBeans() {
    final Bean expected = new Bean();
    final Bean actual = new Bean();

    assertThat(actual, similarTo(expected));
  }

  @Test
  public void withValuesBeans() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void booleanDiff() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    actual.setBooleanValue(false);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void stringDiffOnChild() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    actual.getCollectionOfBeansValue().iterator().next().setStringValue("different");

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void arrayDiff() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    actual.getArrayValue()[0] = "different";

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void arrayOfBeansArrayDiff() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    actual.getArrayOfBeansValue()[0].getArrayValue()[1] = "different";

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void arrayOfBeansArrayOfBeansDiff() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    actual.getArrayOfBeansValue()[1].getArrayOfBeansValue()[0] = createBean(1);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void arrayOfBeansDiffNullOnExpected() {
    final Bean expected = createBean(1);
    final Bean actual = createBean(1);
    expected.setArrayOfBeansValue(null);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void arrayOfBeansDiffNullOnActual() {
    final Bean expected = createBean(1);
    final Bean actual = createBean(1);
    actual.setArrayOfBeansValue(null);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void collectionOfBeansDiffNullOnExpected() {
    final Bean expected = createBean(1);
    final Bean actual = createBean(1);
    expected.setCollectionOfBeansValue(null);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void collectionOfBeansDiffNullOnActual() {
    final Bean expected = createBean(1);
    final Bean actual = createBean(1);
    actual.setCollectionOfBeansValue(null);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void differentClass() {
    final Bean expected = new Bean();
    assertThat("different", similarTo((Object) expected));
  }

  @Test(expected = AssertionError.class)
  public void givenActualMapContainAdditionalEntryMatchingShouldFail() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    actual.getMapValue().put("3", "3");

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void givenExpectedMapContainAdditionalEntryMatchingShouldFail() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    expected.getMapValue().put("3", "3");

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void givenActualMapIsNullAndExpectedMapIsNotNullMatchingShouldFail() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    actual.setMapValue(null);

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void givenExpectedMapIsNullAndActualMapIsNotNullMatchingShouldFail() {
    final Bean expected = createBean(2);
    final Bean actual = createBean(2);
    expected.setMapValue(null);

    assertThat(actual, similarTo(expected));
  }

  @Test
  public void subclass() {
    final Bean expected = new Bean();
    assertThat(new Bean()
    {
    }, similarTo((Object) expected));
  }

  @Test(expected = AssertionError.class)
  public void expectedIsNull() {
    final Bean expected = null;
    final Bean actual = new Bean();

    assertThat(actual, similarTo(expected));
  }

  @Test(expected = AssertionError.class)
  public void actualIsNull() {
    final Bean expected = new Bean();
    final Bean actual = null;

    assertThat(actual, similarTo(expected));
  }

  private Bean createBean(final int depth) {
    final Bean expected = new Bean();
    expected.setBooleanValue(true);
    expected.setIntValue(1);
    expected.setStringValue("1");
    expected.setArrayValue(new String[]{"1", "2"});
    expected.setMapValue(Maps.<String, String>newHashMap());
    expected.getMapValue().put("1", "1");
    expected.getMapValue().put("2", "2");
    if (depth > 0) {
      expected.setArrayOfBeansValue(new Bean[]{createBean(depth - 1), createBean(depth - 1)});
      expected.setCollectionOfBeansValue(Arrays.asList(createBean(depth - 1), createBean(depth - 1)));
    }
    return expected;
  }

  private static class Bean
  {
    private boolean booleanValue;

    private int intValue;

    private String stringValue;

    private String[] arrayValue;

    private Bean[] arrayOfBeansValue;

    private Collection<Bean> collectionOfBeansValue;

    private Map<String, String> mapValue;

    public int getIntValue() {
      return intValue;
    }

    public void setIntValue(int intValue) {
      this.intValue = intValue;
    }

    public String getStringValue() {
      return stringValue;
    }

    public void setStringValue(String stringValue) {
      this.stringValue = stringValue;
    }

    public String[] getArrayValue() {
      return arrayValue;
    }

    public void setArrayValue(String[] arrayValue) {
      this.arrayValue = arrayValue;
    }

    public Collection<Bean> getCollectionOfBeansValue() {
      return collectionOfBeansValue;
    }

    public void setCollectionOfBeansValue(@Nullable Collection<Bean> collectionOfBeansValue) {
      this.collectionOfBeansValue = collectionOfBeansValue;
    }

    public boolean isBooleanValue() {
      return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
      this.booleanValue = booleanValue;
    }

    public Bean[] getArrayOfBeansValue() {
      return arrayOfBeansValue;
    }

    public void setArrayOfBeansValue(@Nullable Bean[] arrayOfBeansValue) {
      this.arrayOfBeansValue = arrayOfBeansValue;
    }

    public Map<String, String> getMapValue() {
      return mapValue;
    }

    public void setMapValue(@Nullable final Map<String, String> mapValue) {
      this.mapValue = mapValue;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("Bean");
      sb.append("{arrayOfBeansValue=").append(
          arrayOfBeansValue == null ? "null" : Arrays.asList(arrayOfBeansValue).toString());
      sb.append(", booleanValue=").append(booleanValue);
      sb.append(", intValue=").append(intValue);
      sb.append(", stringValue='").append(stringValue).append('\'');
      sb.append(", arrayValue=").append(arrayValue == null ? "null" : Arrays.asList(arrayValue).toString());
      sb.append(", collectionOfBeansValue=").append(collectionOfBeansValue);
      sb.append('}');
      return sb.toString();
    }

  }

}
