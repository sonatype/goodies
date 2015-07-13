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
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;

import org.sonatype.sisu.litmus.testsupport.TestData;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.FileVisitResult.TERMINATE;

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

    final Set<File> ignoreDirs = new HashSet<>();

    // interleave search across the different directories; overlays take precedence
    while (!searchDirs.isEmpty()) {
      for (int i = searchDirs.size() - 1; i >= 0; i--) {

        File dir = searchDirs.get(i);
        final File file = findFile(dir, path, ignoreDirs);
        if (file != null) {
          return file;
        }

        ignoreDirs.add(dir); // skip this sub-tree when searching from parent

        dir = dir.getParentFile();
        if (dir == null || dir.equals(dataDirs.get(i).getParentFile())) {
          searchDirs.remove(i);
        }
        else {
          searchDirs.set(i, dir);
        }
      }
    }

    throw new MissingResourceException(String.format("Path %s not found in %s searching from %s/%s upwards",
        path, dataDirs, asPath(description.getTestClass()), mn(description.getMethodName())), null, null);
  }

  /**
   * Finds the file that matches the given path relative to the given root directory.
   *
   * @param rootDir root directory to search from
   * @param path path to look up (supports globbed syntax)
   * @param ignoreDirs directories to ignore
   * @return matching file; {@code null} if no match was found
   */
  private File findFile(final File rootDir, final String path, final Set<File> ignoreDirs) {
    if (!isGlobbedPath(path)) {
      final File file = file(rootDir, path);
      return file.exists() ? file : null;
    }

    final File[] result = new File[1];
    final PathMatcher matcher = asGlobbedMatcher(path);
    final Path rootPath = rootDir.toPath();

    try {
      Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>()
      {
        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
          return ignoreDirs.contains(dir.toFile()) ? SKIP_SUBTREE : CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
          if (matcher.matches(rootPath.relativize(file))) {
            result[0] = file.toFile();
            return TERMINATE;
          }
          return CONTINUE;
        }
      });
    }
    catch (final IOException e) {
      failed(e, description);
    }

    return result[0];
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
   * Returns {@code true} if path looks like a globbed path; otherwise {@code false}
   */
  private static boolean isGlobbedPath(final String path) {
    for (final char c : path.toCharArray()) {
      if (c == '*' || c == '?' || c == '[' || c == '{') {
        return true;
      }
    }
    return false;
  }

  /**
   * Return a package matcher for the given globbed path.
   *
   * @param path globbed path
   * @return patch matcher
   */
  private static PathMatcher asGlobbedMatcher(final String path) {
    final String pattern;
    if (path.startsWith("**/")) {
      // workaround for matching relative roots
      pattern = "glob:{**/,}" + path.substring(3);
    }
    else {
      pattern = "glob:" + path;
    }
    return FileSystems.getDefault().getPathMatcher(pattern);
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
    final int bracketIndex = methodName.indexOf('[');
    if (bracketIndex >= 0) {
      return methodName.substring(0, bracketIndex);
    }
    return methodName;
  }

}
