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

import javax.xml.namespace.QName;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;

/**
 * Adds {@link XStreamAlias} to generated types.
 *
 * @since jacksbee 1.0
 */
public class XStreamAliasPlugin
    extends AbstractParameterizablePlugin
{
  @Override
  public String getOptionName() {
    return "XxstreamAlias";
  }

  @Override
  public String getUsage() {
    return "Adds @XStreamAlias to generated types.";
  }

  @Override
  protected boolean run(final Outline outline, final Options options) throws Exception {
    assert outline != null;
    assert options != null;

    for (ClassOutline type : outline.getClasses()) {
      QName qname = type.target.getElementName();
      if (qname == null) {
        qname = type.target.getTypeName();
      }
      if (qname != null) {
        addAlias(type.implClass, qname);
      }
    }

    for (EnumOutline type : outline.getEnums()) {
      QName qname = type.target.getTypeName();
      if (qname != null) {
        addAlias(type.clazz, qname);
      }
    }

    return true;
  }

  private void addAlias(final JDefinedClass type, final QName qname) {
    assert type != null;
    assert qname != null;
    JAnnotationUse anno = type.annotate(XStreamAlias.class);
    anno.param("value", qname.getLocalPart());
  }
}