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
package org.sonatype.sisu.litmus.testsupport.junit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonatype.sisu.litmus.testsupport.TestData;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * JUnit rule for accessing test data
 *
 * @since litmus 1.4
 */
public class TestDataRule
    extends TestWatcher
    implements TestData
{

  /**
   * The root directories containing test data.
   */
  private final List<File> dataDirs = new ArrayList<>();

  /**
   * Test description.
   * Set when test starts.
   */
  private Description description;

  /**
   * Constructor.
   *
   * @param dataDir root directory containing test data. Cannot be null.
   * @param additionalDirs additional directories to overlay on the primary.
   */
  public TestDataRule(final File dataDir, final File... additionalDirs) {
    dataDirs.add(checkNotNull(dataDir));
    for (final File dir : additionalDirs) {
      dataDirs.add(checkNotNull(dir));
    }
  }

  /**
   * Overlays the given directory on top of the current test data.
   *
   * @param dataDir directory containing test data.
   */
  public void addDirectory(final File dataDir) {
    dataDirs.add(checkNotNull(dataDir));
  }

  @Override
  protected void starting(final Description description) {
    this.description = checkNotNull(description);
  }

  @Override
  public File resolveFile(final String path) {
    checkState(description != null, "Test was not yet initialized");

    final List<File> searchDirs = new ArrayList<>(dataDirs.size());
    for (final File dir : dataDirs) {
      searchDirs.add(file(dir, asPath(description.getTestClass()), mn(description.getMethodName())));
    }

    // interleave search across the different directories; overlays take precedence
    while (!searchDirs.isEmpty()) {
      for (int i = searchDirs.size() - 1; i >= 0; i--) {

        File dir = searchDirs.get(i);
        final File file = file(dir, path);
        if (file.exists()) {
          return file;
        }
        dir = dir.getParentFile();
        if (dir == null || dir.equals(dataDirs.get(i).getParentFile())) {
          searchDirs.remove(i);
        }
        else {
          searchDirs.set(i, dir);
        }
      }
    }

    throw new RuntimeException(String.format("Path %s not found in %s searching from %s/%s upwards",
        path, dataDirs, asPath(description.getTestClass()), mn(description.getMethodName())));
  }

  /**
   * Return a file path to a class (replaces "." with "/")
   *
   * @param clazz class to get the path for
   * @return path to class
   * @since litmus 1.0
   */
  //@TestAccessible
  static String asPath(final Class<?> clazz) {
    return asPath(clazz.getPackage()) + "/" + clazz.getSimpleName();
  }

  /**
   * Return a file path from a package (replaces "." with "/")
   *
   * @param pkg package to get the path for
   * @return package path
   */
  private static String asPath(final Package pkg) {
    return pkg.getName().replace(".", "/");
  }

  /**
   * File builder
   *
   * @param root  starting root
   * @param paths paths to append
   * @return a file starting from root and appended sub-paths
   */
  private static File file(final File root, final String... paths) {
    File file = root;
    for (String path : paths) {
      file = new File(file, path);
    }
    return file;
  }

  /**
   * Drops index part from name in case of a parametrized test.
   *
   * @param methodName method name
   * @return index-less method name
   */
  private static String mn(final String methodName) {
    if (methodName.contains("[")) {
      return methodName.substring(0, methodName.indexOf("["));
    }
    return methodName;
  }

}
