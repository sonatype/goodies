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

package org.sonatype.sisu.litmus.testsupport;

import java.io.File;

/**
 * Test data access.
 *
 * @since 1.4
 */
public interface TestData
{

  /**
   * Resolves a test data file by looking up the specified path into data directory.
   * <p/>
   * It searches the following path locations:<br/>
   * {@code <dataDir>/<test class package>/<test class name>/<test method name>/</path>}<br/>
   * {@code all parent directories of above up to and including <dataDir>}
   *
   * @param path path to look up
   * @return found file
   */
  File resolveFile(String path);

}
