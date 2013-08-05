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

package org.sonatype.sisu.goodies.validation.internal;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @since 1.6
 */
@Named
@Singleton
public class Service
{

  public void method1() {
  }

  public void method2(final Foo foo) {
  }

  public void method3(final @Valid Foo foo) {
  }

  @NotNull
  public String method4(final @Valid Foo foo) {
    return null;
  }

  public void method5(final @NotNull String value) {
  }

  public void method6(final @NotNull String value, final @Valid Foo foo) {
  }

  public void method7(final @NotNull String value, final @NotNull @Valid Foo foo) {
  }

}
