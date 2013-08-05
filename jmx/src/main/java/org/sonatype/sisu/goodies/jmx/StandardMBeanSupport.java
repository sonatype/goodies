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

package org.sonatype.sisu.goodies.jmx;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for {@link StandardMBean} implementations.
 *
 * @since 1.5
 */
public class StandardMBeanSupport
    extends StandardMBean
{
  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected <T> StandardMBeanSupport(final T impl, final Class<T> intf) throws NotCompliantMBeanException {
    super(impl, intf);
  }

  protected StandardMBeanSupport(final Class<?> intf) throws NotCompliantMBeanException {
    super(intf);
  }

  protected <T> StandardMBeanSupport(final T impl, final Class<T> intf, final boolean mxbean) {
    super(impl, intf, mxbean);
  }

  protected StandardMBeanSupport(final Class<?> intf, final boolean mxbean) {
    super(intf, mxbean);
  }
}
