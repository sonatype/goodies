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

package org.sonatype.sisu.litmus.testsupport.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Here we test MockitoRule actually initializes mock objects as we expect.
 * <p>
 * Note: @InjectMocks only is supposed to work with @Spy , not @Mock
 * </p>
 *
 * @since 1.3
 */
public class MockitoRuleTest
{

  @Rule
  public MockitoRule mockitoRule = new MockitoRule(this);


  @Mock
  private HashMap mockedMap;

  @Spy
  private LinkedHashMap<String, String> spyMap;

  @Spy
  private HashSet<String> spySet;

  @Captor
  private ArgumentCaptor<String> captor;

  @InjectMocks
  private MockitoRulePropertyBean propertyBean;

  @InjectMocks
  private MockitoRuleFieldBean fieldBean;

  @InjectMocks
  private MockitoRuleConstructorBean constructorBean;

  private static HashMap<String, String> staticMap = new HashMap<String, String>();

  @Test
  public void membersWithMockAnnotationWereMocked() throws Exception {
    assertThat(staticMap, notNullValue());
    assertThat(mockedMap, notNullValue());
    assertThat(staticMap, not(sameInstance(mockedMap)));
    staticMap = mockedMap;
  }

  @Test
  public void membersWithMockAnnotationWereMockedPerTest() throws Exception {
    assertThat(staticMap, notNullValue());
    assertThat(mockedMap, notNullValue());
    assertThat(staticMap, not(sameInstance(mockedMap)));
    staticMap = mockedMap;
  }

  @Test
  public void membersWithSpyAnnotationWereMocked() throws Exception {
    assertThat(spyMap, notNullValue());
    assertThat(spySet, notNullValue());

  }

  @Test
  public void membersWithCaptorAnnotationWereMocked() throws Exception {
    assertThat(captor, notNullValue());
  }

  @Test
  public void membersWithInjectMocksWereInjectedWithMocksBySetter() throws Exception {
    assertThat(propertyBean, notNullValue());
    assertThat(propertyBean.getSpyMap(), sameInstance(spyMap));
    assertThat(propertyBean.getSpySet(), sameInstance(spySet));
  }

  @Test
  public void membersWithInjectMocksWereInjectedWithMocksByProperty() throws Exception {
    assertThat(fieldBean, notNullValue());
    assertThat(fieldBean.getSpyMap(), sameInstance(spyMap));
    assertThat(fieldBean.getSpySet(), sameInstance(spySet));
  }

  @Test
  public void membersWithInjectMocksWereInjectedWithMocksByConstructor() throws Exception {
    assertThat(constructorBean, notNullValue());
    assertThat(constructorBean.getSpyMap(), sameInstance(spyMap));
    assertThat(constructorBean.getSpySet(), sameInstance(spySet));
  }
}
