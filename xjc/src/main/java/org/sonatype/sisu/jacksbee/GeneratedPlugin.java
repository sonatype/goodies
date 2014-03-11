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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Generated;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.Driver;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;

/**
 * Adds {@link Generated} to generated types.
 *
 * @since jacksbee 1.0
 */
public class GeneratedPlugin
    extends AbstractParameterizablePlugin
{
  public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  @Override
  public String getOptionName() {
    return "Xgenerated";
  }

  @Override
  public String getUsage() {
    return "Adds @Generated to generated types.";
  }

  @Override
  protected boolean run(final Outline outline, final Options options) throws Exception {
    assert outline != null;
    assert options != null;

    for (ClassOutline type : outline.getClasses()) {
      addGenerated(type.implClass);
    }

    for (EnumOutline type : outline.getEnums()) {
      addGenerated(type.clazz);
    }

    return true;
  }

  private void addGenerated(final JDefinedClass type) {
    assert type != null;

    JAnnotationUse anno = type.annotate(Generated.class);
    anno.param("value", String.format("XJC %s", Driver.getBuildID()));

    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT);
    anno.param("date", sdf.format(now));

    // TODO: Maybe support customized comments?
  }
}