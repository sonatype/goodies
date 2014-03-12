/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.jacksbee;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;
import org.w3c.dom.Attr;

/**
 * Base class for plugins creating identity methods (equals and hashcode).
 *
 * @since jacksbee 1.0
 */
public abstract class AbstractIdentityBuilderPlugin
    extends AbstractParameterizablePlugin
{
  public static final String NAMESPACE_URI = "http://sonatype.org/jacksbee/id-builder";

  private static final String TAG_EXCLUDE = "exclude";

  private static final String ATTRIBUTE_FIELDS = "fields";

  private static final String FIELD_DELIMITER = ",";

  private List<String> excludedFields;

  @Override
  public Collection<QName> getCustomizationElementNames() {
    return Arrays.asList(new QName(NAMESPACE_URI, TAG_EXCLUDE));
  }

  @Override
  protected boolean run(final Outline outline, final Options options) throws Exception {
    assert outline != null;
    assert options != null;

    for (ClassOutline type : outline.getClasses()) {
      // Set-up data used for processing.
      excludedFields = findFieldsForTag(type, TAG_EXCLUDE);
      processClassOutline(type);
    }

    return true;
  }

  protected boolean isFieldApplicable(FieldOutline field) {
    return !excludedFields.contains(field.getPropertyInfo().getName(false));
  }

  abstract void processClassOutline(ClassOutline type);

  private List<String> findFieldsForTag(final ClassOutline outline, final String tag) {
    List<String> fields = Collections.emptyList();

    CPluginCustomization customization = outline.target.getCustomizations().find(NAMESPACE_URI, tag);
    if (customization != null) {
      Attr attributeNode = customization.element.getAttributeNode(ATTRIBUTE_FIELDS);
      if (attributeNode != null) {
        customization.markAsAcknowledged();

        String fieldsValue = attributeNode.getValue();
        fields = Arrays.asList(fieldsValue.replace(" ", "").split(FIELD_DELIMITER));
      }
    }
    return fields;
  }
}