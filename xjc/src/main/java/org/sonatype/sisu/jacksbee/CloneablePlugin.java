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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;

/**
 * Adds {@link Cloneable} and {@link Object#clone} implementation to all classes.
 *
 * @since jacksbee 1.0
 */
public class CloneablePlugin
    extends AbstractParameterizablePlugin
{
  @Override
  public String getOptionName() {
    return "Xcloneable";
  }

  @Override
  public String getUsage() {
    return "Adds Cloneable and Object.clone() implementation to all classes.";
  }

  @Override
  protected boolean run(final Outline outline, final Options options) throws Exception {
    assert outline != null;
    assert options != null;

    for (ClassOutline type : outline.getClasses()) {
      processClassOutline(type);
    }

    return true;
  }

  private void processClassOutline(final ClassOutline outline) {
    assert outline != null;

    JDefinedClass type = outline.implClass;
    JCodeModel model = type.owner();
    type._implements(model.ref(Cloneable.class));

    JMethod method = type.method(JMod.PUBLIC, outline.implRef, "clone");
    method.annotate(Override.class);

    JBlock body = method.body();
    JTryBlock block = body._try();
    block.body()._return(JExpr.cast(outline.implRef, JExpr._super().invoke("clone")));
    block._catch(model.ref(CloneNotSupportedException.class)).body()._throw(JExpr._new(model.ref(InternalError.class)));
  }
}