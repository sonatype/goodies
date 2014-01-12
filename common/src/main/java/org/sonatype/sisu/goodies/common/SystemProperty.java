/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.goodies.common;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to access System properties.
 */
public class SystemProperty
{
  private static final Logger log = LoggerFactory.getLogger(SystemProperty.class);

  private final String name;

  public SystemProperty(final String name) {
    this.name = checkNotNull(name);
  }

  public SystemProperty(final Class type, final String suffix) {
    this(checkNotNull(type, "type").getName() + "." + checkNotNull(suffix, "suffix"));
  }

  @VisibleForTesting
  Properties properties() {
    return System.getProperties();
  }

  public String name() {
    return name;
  }

  public void set(final @Nullable Object value) {
    String str = String.valueOf(value);
    properties().setProperty(name, str);
    log.trace("Set {}={}", name, str);
  }

  public boolean isSet() {
    return get() != null;
  }

  @Nullable
  public String get() {
    return properties().getProperty(name);
  }

  public String get(final String defaultValue) {
    String value = get();
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public <T> T get(final Class<T> type) {
    checkNotNull(type);
    PropertyEditor editor = PropertyEditorManager.findEditor(type);
    if (editor == null) {
      // HACK: Ensure we can always convert Boolean/Integer, seems like some JVM configurations can not do this?!
      if (type == Boolean.class) {
        editor = new BooleanPropertyEditor();
      }
      else if (type == Integer.class) {
        editor = new IntegerPropertyEditor();
      }
      else {
        throw new RuntimeException("No property-editor for type: " + type.getName());
      }
    }
    String value = get();
    if (value == null) {
      return null;
    }
    editor.setAsText(value);
    return (T) editor.getValue();
  }

  public <T> T get(final Class<T> type, final T defaultValue) {
    T value = get(type);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  public void remove() {
    properties().remove(name);
    log.trace("Remove: {}", name);
  }

  // TODO: This could probably be converted into a PropertyEditor, for now leave as custom code

  public List<String> asList() {
    String value = get();
    if (value == null) {
      return Collections.emptyList();
    }
    String[] items = value.split(",");
    List<String> result = Lists.newArrayListWithCapacity(items.length);
    for (String item : items) {
      result.add(item.trim());
    }
    return result;
  }

  @Override
  public String toString() {
    return name + "=" + get();
  }

  private static class TextPropertyEditorSupport
      extends PropertyEditorSupport
  {
    @Override
    public void setAsText(final String text) {
      super.setValue(text);
    }
  }

  private static class BooleanPropertyEditor
    extends TextPropertyEditorSupport
  {
    @Override
    public Object getValue() {
      return Boolean.valueOf(getAsText());
    }
  }

  private static class IntegerPropertyEditor
      extends TextPropertyEditorSupport
  {
    @Override
    public Object getValue() {
      return Integer.valueOf(getAsText());
    }
  }
}
