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

package org.sonatype.sisu.jacksbee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.DOMUtils;
import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;

/**
 * Allows arbitrary code to be inserted into generated types.
 *
 * @since jacksbee 1.0
 */
public class CodeInsertPlugin
    extends AbstractParameterizablePlugin
{
  public static final String NS = "http://sonatype.org/jacksbee/code-insert";

  public static final String CODE = "code";

  public static final String IMPORT = "import";

  @Override
  public String getOptionName() {
    return "XcodeInsert";
  }

  @Override
  public String getUsage() {
    return "Allows arbitrary code to be inserted into generated types.";
  }

  @Override
  public Collection<QName> getCustomizationElementNames() {
    return Arrays.asList(new QName(NS, CODE), new QName(NS, IMPORT));
  }

  @Override
  protected boolean run(final Outline outline, final Options options) throws Exception {
    assert outline != null;
    assert options != null;

    for (ClassOutline type : outline.getClasses()) {
      process(type.implClass, type.target.getCustomizations(), type.parent());
    }

    for (EnumOutline type : outline.getEnums()) {
      process(type.clazz, type.target.getCustomizations(), type.parent());
    }

    return true;
  }

  private void process(final JDefinedClass type, final CCustomizations customizations, final Outline parent) {
    processImport(type, customizations, parent);
    processCode(type, customizations, parent);
  }

  private void processImport(final JDefinedClass type, final CCustomizations customizations, final Outline parent) {
    assert type != null;
    assert customizations != null;
    assert parent != null;

    CPluginCustomization custom = customizations.find(NS, IMPORT);
    if (custom != null) {
      custom.markAsAcknowledged();

      String body = DOMUtils.getElementText(custom.element);
      String[] tmp = body.trim().split("\\s+");
      List<String> names = new ArrayList<String>();

      // Build a list of class names
      for (String name : tmp) {
        if (name.trim().length() == 0) {
          continue;
        }
        names.add(name);
      }

      // Only generate the dummy block if we have some names to import
      if (!names.isEmpty()) {
        // Setup dummy static + if(false) to force XJC to import
        JBlock block = type.init()._if(JExpr.direct("false"))._then();

        // Add dummy toString() call for each imported class
        for (String name : names) {
          JClass ref = parent.getCodeModel().ref(name);
          // Generate dummy reference to force import
          block.invoke(ref.dotclass(), "toString");
        }
      }
    }
  }

  private void processCode(final JDefinedClass type, final CCustomizations customizations, final Outline parent) {
    assert type != null;
    assert customizations != null;
    assert parent != null;

    CPluginCustomization custom = customizations.find(NS, CODE);
    if (custom != null) {
      custom.markAsAcknowledged();
      String body = DOMUtils.getElementText(custom.element);
      type.direct(body);
    }
  }
}