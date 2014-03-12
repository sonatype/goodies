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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

/**
 * Adds {@link Object#hashCode} methods to all classes.
 *
 * @since jacksbee 1.0
 */
public class HashCodeBuilderPlugin
    extends AbstractIdentityBuilderPlugin
{
  public static final String COMMONS = "org.apache.commons.lang.builder.HashCodeBuilder";

  public static final String GWT = "com.flipthebird.gwthashcodeequals.HashCodeBuilder";

  private String builderType = COMMONS;

  public String getBuilderType() {
    return builderType;
  }

  public void setBuilderType(final String builderType) {
    assert builderType != null;
    this.builderType = builderType;
  }

  @Override
  public String getOptionName() {
    return "XhashCodeBuilder";
  }

  @Override
  public String getUsage() {
    return "Adds hashCode() to all classes.";
  }

  protected void processClassOutline(final ClassOutline outline) {
    assert outline != null;

    JDefinedClass type = outline.implClass;
    JCodeModel model = type.owner();

    JMethod method = type.method(JMod.PUBLIC, model.INT, "hashCode");
    method.annotate(Override.class);

    JBlock body = method.body();
    JType builderType = model.ref(getBuilderType());
    JVar builder = body.decl(JMod.FINAL, builderType, "builder", JExpr._new(builderType));

    // Use accessors because collection based fields are lazy initialized to
    // empty collections on access and won't always be equal via field access.
    // I.e. null vs empty collections.
    ClassOutlineHelper helper = new ClassOutlineHelper(outline);
    for (FieldOutline field : outline.getDeclaredFields()) {
      if (isFieldApplicable(field)) {
        JMethod accessor = helper.findAccessor(field);
        if (accessor != null) {
          body.add(builder.invoke("append").arg(JExpr._this().invoke(accessor)));
        }
      }
    }

    body._return(builder.invoke("build"));
  }
}