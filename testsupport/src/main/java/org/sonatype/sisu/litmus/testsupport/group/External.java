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

package org.sonatype.sisu.litmus.testsupport.group;

/**
 * Marker interface to categorize a test as requiring some sort of external resource to properly run.
 * <p/>
 * An external resource is a resource not controlled directly by the test or its fixture, for example a public website
 * or
 * internal database or email server running on another host. The resource is not external if it is launched by the
 * testing framework that sets up the test.
 *
 * <p/>
 * External tests should obviously be avoided and are a sign of code smell. But if you have them, it is useful to at
 * least mark them and perhaps run them only on systems which will support the test requirements.
 *
 * @since 1.5
 */
public interface External
{

}
