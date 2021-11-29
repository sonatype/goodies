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
package org.sonatype.sisu.goodies.testsupport.junit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import org.sonatype.sisu.goodies.testsupport.TestSupport;
import org.sonatype.sisu.goodies.testsupport.hamcrest.FileMatchers;
import org.sonatype.sisu.goodies.testsupport.junit.TestIndexRule;
import org.sonatype.sisu.goodies.testsupport.junit.TestInfoRule;

import com.google.common.io.Files;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.sonatype.sisu.goodies.testsupport.hamcrest.FileMatchers.contains;
import static org.sonatype.sisu.goodies.testsupport.hamcrest.FileMatchers.doesNotContain;
import static org.sonatype.sisu.goodies.testsupport.hamcrest.FileMatchers.exists;
import static org.sonatype.sisu.goodies.testsupport.hamcrest.FileMatchers.isDirectory;

/**
 * {@link TestIndexRule} UTs.
 */
public class TestIndexRuleTest
    extends TestSupport
{

  public static final boolean THREAD_IS_NOT_INTERRUPTED = true;

  private File indexRoot = util.resolveFile("target/test-index-rule-index");

  private File dataRoot = util.resolveFile("target/test-index-rule-data");

  @Rule
  public TestInfoRule testInfo = new TestInfoRule();

  @Rule
  public TestIndexRule underTest = new TestIndexRule(indexRoot, dataRoot);

  /**
   * Verifies that a root directory is created.
   */
  @Test
  public void rootDirIsCreated() {
    final File rootDir = underTest.getDirectory();
    assertThat(rootDir, exists());
    assertThat(rootDir, isDirectory());
  }

  /**
   * Verifies that a sub dir directory is created.
   */
  @Test
  public void subDirIsCreated() {
    final File fooDir = underTest.getDirectory("foo");
    assertThat(fooDir, exists());
    assertThat(fooDir, isDirectory());
    assertThat(fooDir.getParentFile(), is(equalTo(underTest.getDirectory())));
  }

  /**
   * Verifies that a index.xml and friends are created.
   */
  @Test
  public void indexXmlAndFriendsAreCreated() {
    // index.xml are created lazy on first usage
    underTest.getDirectory();

    assertThat(new File(indexRoot, "index.xml"), exists());
    assertThat(new File(indexRoot, "index.css"), exists());
    assertThat(new File(indexRoot, "index.xsl"), exists());
  }

  /**
   * Verifies that information is recorded.
   */
  @Test
  public void recordInfo() {
    underTest.recordInfo("info", testInfo.getMethodName());
    underTest.save();
    assertThat(new File(indexRoot, "index.xml"), contains(testInfo.getMethodName()));
  }

  /**
   * Verifies that last information is recorded for a key.
   */
  @Test
  public void recordOnlyLastInfo() {
    underTest.recordInfo("info", "Initial " + testInfo.getMethodName());
    underTest.recordInfo("info", "Updated " + testInfo.getMethodName());
    underTest.save();
    assertThat(new File(indexRoot, "index.xml"), contains("Updated " + testInfo.getMethodName()));
    assertThat(new File(indexRoot, "index.xml"), doesNotContain("Initial " + testInfo.getMethodName()));
  }

  /**
   * Verifies that a relative path is recorded as info.
   */
  @Test
  public void recordLinkAboutFileInIndexDir() {
    underTest.recordLink("info", underTest.getDirectory("some-dir"));
    underTest.save();
    assertThat(new File(indexRoot, "index.xml"), contains("some-dir"));
  }

  /**
   * Verifies that a relative path is recorded as info.
   */
  @Test
  public void recordLinkAboutFileNotInIndexDir() {
    final File file = util.resolveFile("target/some-dir");
    file.mkdirs();
    underTest.recordLink("info", file);
    underTest.save();
    assertThat(new File(indexRoot, "index.xml"), contains("../some-dir"));
  }

  /**
   * Calculate relative path from a dir to a sub dir.
   */
  @Test
  public void relativePath01()
      throws IOException
  {
    final File from = new File("a/b/c");
    final File to = new File("a/b/c/d");

    final String relativePath = TestIndexRule.calculateRelativePath(from, to);
    assertThat(relativePath, is(equalTo("d")));
  }

  /**
   * Calculate relative path from a sub dir to a parent dir.
   */
  @Test
  public void relativePath02()
      throws IOException
  {
    final File from = new File("a/b/c/d");
    final File to = new File("a/b/c");

    final String relativePath = TestIndexRule.calculateRelativePath(from, to);
    assertThat(relativePath, is(equalTo("..")));
  }

  /**
   * Calculate relative path from a sub dir to a sub dir or same parent dir.
   */
  @Test
  public void relativePath03()
      throws IOException
  {
    final File from = new File("a/b/c/d");
    final File to = new File("a/b/e/f");

    final String relativePath = TestIndexRule.calculateRelativePath(from, to);
    assertThat(relativePath, is(equalTo("../../e/f")));
  }

  /**
   * Verify if a file with same name is copied twice, and source is not from data dir, an timestamped file is
   * created.
   */
  @Test
  @Ignore("This test is unstable")
  public void recordAndCopyLinkMultipleTimes() {
    final File fileToCopy = util.resolveFile("src/test/resources/sized_file.txt");
    underTest.recordAndCopyLink(testInfo.getMethodName(), fileToCopy);
    underTest.recordAndCopyLink(testInfo.getMethodName(), fileToCopy);
    final File indexTargetDir = new File(indexRoot, underTest.getDirectory().getName());
    final String[] files = indexTargetDir.list(new FilenameFilter()
    {
      @Override
      public boolean accept(final File parentDir, final String name) {
        return name.matches("sized_file-.*\\.txt");
      }
    });
    // NOTE this count may fail on windows VM occasionally if writing files is slow, but should more often pass
    assertThat(files, arrayWithSize(2));
  }

  /**
   * Verify that copied file is copied under same path structure as source file f source is in data dir.
   */
  @Test
  public void recordAndCopyLinkMultipleTimesFromDataDir()
      throws IOException
  {
    final File source = util.resolveFile("src/test/resources/sized_file.txt");
    final File target = new File(underTest.getDirectory("foo/bar"), "foo.txt");

    Files.copy(source, target);

    underTest.recordAndCopyLink(testInfo.getMethodName(), target);

    final File indexTargetDir = new File(indexRoot, underTest.getDirectory().getName());
    assertThat(new File(indexTargetDir, "foo/bar/foo.txt"), FileMatchers.exists());
  }

  @Test
  public void recordAndCopyLinkWhileWriting()
      throws IOException, InterruptedException
  {
    final AtomicReference<Exception> failure = new AtomicReference<Exception>();
    final File outFile = new File(underTest.getDirectory(), "write.txt");
    final Thread writerThread = new Thread()
    {
      @Override
      public void run() {
        try {
          final PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
          while (THREAD_IS_NOT_INTERRUPTED) {
            out.println(new Date());
            out.flush();
          }
        }
        catch (final FileNotFoundException e) {
          failure.set(e);
        }
      }
    };
    final Thread copyThread = new Thread()
    {
      @Override
      public void run() {
        final Random randomGenerator = new Random();
        final long start = System.currentTimeMillis();
        try {
          while (System.currentTimeMillis() - start < 10000) {
            underTest.recordAndCopyLink("test", outFile);
            sleep(randomGenerator.nextInt(300));
          }
        }
        catch (final Exception e) {
          failure.set(e);
        }
      }
    };
    writerThread.start();
    copyThread.start();
    copyThread.join();
    writerThread.interrupt();
    if (failure.get() != null) {
      assertThat("Copy failed: " + failure.get(), false);
    }
  }

}
