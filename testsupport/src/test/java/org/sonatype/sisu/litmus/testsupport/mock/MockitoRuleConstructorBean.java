/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.litmus.testsupport.mock;

import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Tests @InjectMocks constructor injection
 *
 * @see MockitoRuleTest
 */
public class MockitoRuleConstructorBean
{

  private HashSet<String> spySet;

  private LinkedHashMap<String, String> spyMap;

  public MockitoRuleConstructorBean(HashSet<String> spySet, LinkedHashMap<String, String> spyMap) {
    this.spySet = spySet;
    this.spyMap = spyMap;
  }

  // no-arg constructor required for setter inject
  public MockitoRuleConstructorBean() {
  }

  public HashSet<String> getSpySet() {
    return spySet;
  }

  public LinkedHashMap<String, String> getSpyMap() {
    return spyMap;
  }
}
