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
 * Test related directory index.
 *
 * @since 1.4
 */
public interface TestIndex
{

  /**
   * Returns a test specific directory of format {@code <indexDir>/<counter>}.
   * If directory does not exist yet, it will be created.
   *
   * @return test specific directory
   */
  File getDirectory();

  /**
   * Returns a test specific directory of format {@code <indexDir>/<counter>/<name>}.
   * If directory does not exist yet, it will be created.
   *
   * @param name name of test specific directory
   * @return test specific directory. Never null
   */
  File getDirectory(String name);

  /**
   * Records information about current running test.
   *
   * @param key   information key
   * @param value information value
   */
  void recordInfo(String key, String value);

  /**
   * Records information about current running test.
   * The value is considered to be a link.
   *
   * @param key   information key
   * @param value information value
   */
  void recordLink(String key, String value);

  /**
   * Records information about current running test.
   * The value is considered to be a link to a file and value will be stored as relative to index root dir.
   * No link is recorded if file does not exist.
   *
   * @param key  information key (cannot be null)
   * @param file linked file (cannot be null)
   */
  void recordLink(String key, File file);

  /**
   * Copies and records information about current running test.
   * The value is considered to be a link to a file and value will be stored as relative to index root dir.
   * No link is recorded if file does not exist.
   *
   * @param key  information key
   * @param file linked file
   */
  void recordAndCopyLink(String key, File file);

}