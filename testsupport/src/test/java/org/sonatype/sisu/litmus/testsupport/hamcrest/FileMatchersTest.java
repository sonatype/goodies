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

package org.sonatype.sisu.litmus.testsupport.hamcrest;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.litmus.testsupport.mock.MockitoRule;

import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;

/**
 * @author plynch
 */
public class FileMatchersTest
    extends TestSupport
{
  public MockitoRule MockitoRule = new MockitoRule(this);

  private static File REAL_FILE;

  private static File REAL_DIR;

  private static File NON_EXISTING_FILE;

  @Mock
  private File mockFile;

  @Before
  public void setupFiles()
      throws Exception
  {
    REAL_FILE = new File(util.getBaseDir(), "pom.xml");
    assertThat(REAL_FILE.isFile(), is(true));
    assertThat(REAL_FILE.canRead(), is(true));
    assertThat(REAL_FILE.canWrite(), is(true));

    REAL_DIR = new File(System.getProperty("basedir"));
    assertThat(REAL_DIR.isDirectory(), is(true));
    NON_EXISTING_FILE = new File(REAL_DIR, "/DOESNOTEXIST");
    assertThat(NON_EXISTING_FILE.exists(), is(false));
  }

  @Test
  public void isDirectoryMatchesDirectory() {
    assertThat(REAL_DIR, FileMatchers.isDirectory());
  }

  @Test
  public void isDirectoryDoesNotMatchFile() {
    assertThat(REAL_FILE, not(FileMatchers.isDirectory()));
  }

  @Test
  public void isFileDoesNotMatchDirectory() {
    assertThat(REAL_DIR, not(FileMatchers.isFile()));
  }

  @Test
  public void isFileMatchesFile() {
    assertThat(REAL_FILE, FileMatchers.isFile());
  }

  @Test
  public void existsFileThatDoesNotExist() {
    assertThat(NON_EXISTING_FILE, not(FileMatchers.exists()));
  }

  @Test
  public void existsDirThatExists() {
    assertThat(REAL_DIR, FileMatchers.exists());
  }

  @Test
  public void writableMatchesWritable() {
    assertThat(REAL_FILE, FileMatchers.writable());
  }

  @Test
  public void readableMatchesReadable() {
    assertThat(REAL_FILE, FileMatchers.writable());
  }

  @Test
  public void named() {
    assertThat(REAL_FILE, FileMatchers.named("pom.xml"));
  }

  @Test
  public void withCanonicalPath() {
    assertThat(REAL_FILE, not(FileMatchers.withCanonicalPath("pom.xml")));
  }

  @Test
  public void withAbsolutePath() {
    assertThat(REAL_FILE, not(FileMatchers.withAbsolutePath("pom.xml")));
  }

  @Test
  public void sizedWithMatcher() {
    assertThat(REAL_FILE, FileMatchers.sized(Matchers.greaterThan(100L)));
  }

  @Test
  public void sizedStandalone() {
    File sizedFile = new File(REAL_DIR, "src/test/resources/sized_file.txt");
    assertThat(sizedFile, FileMatchers.exists());
    assertThat(sizedFile, FileMatchers.sized(21L));
  }

  @Test
  public void contains() {
    File file = new File(REAL_DIR, "src/test/resources/file.properties");
    assertThat(file, FileMatchers.contains("x=y", "foo=bar"));
  }

  @Test
  public void doesNotContain() {
    File file = new File(REAL_DIR, "src/test/resources/file.properties");
    assertThat(file, FileMatchers.doesNotContain("y=x", "bar=foo"));
  }

  @Test
  public void containsOnly() {
    File file = new File(REAL_DIR, "src/test/resources/sized_file.txt");
    assertThat(file, FileMatchers.containsOnly("A File of fixed size\n"));
  }

  @Test
  public void containsEntry()
      throws IOException
  {
    File file = new File(REAL_DIR, "src/test/resources/test.jar");
    assertThat(new ZipFile(file), FileMatchers.containsEntry("META-INF/MANIFEST.MF"));
    assertThat(new ZipFile(file), FileMatchers.containsEntry("META-INF/"));
  }

  @Test
  public void containsEntryDescription() {
    final StringDescription description = new StringDescription();
    FileMatchers.containsEntry("containsEntryDescription").describeTo(description);
    assertThat(description.toString(), containsString("containsEntryDescription"));
  }

  @Test
  public void isEmptyDirectory()
      throws IOException
  {
    when(mockFile.list()).thenReturn(new String[]{});
    when(mockFile.isDirectory()).thenReturn(true);
    // sugar
    when(mockFile.getAbsolutePath()).thenReturn("/mydir");
    assertThat(mockFile, FileMatchers.isEmptyDirectory());
  }

  @Test
  public void isEmptyDirectoryFailsOnNonDirectory()
      throws IOException
  {
    try {
      assertThat(REAL_FILE, FileMatchers.isEmptyDirectory());
    }
    catch (AssertionError ae) {
      assertThat(ae.getMessage(), startsWith("\nExpected: a directory\n     but: found a non directory at"));
    }
  }

  @Test
  public void isEmptyDirectoryFailsOnNonEmptyDirectory()
      throws IOException
  {
    when(mockFile.list()).thenReturn(new String[]{"foo.txt", "bar"});
    when(mockFile.isDirectory()).thenReturn(true);
    // sugar
    when(mockFile.getAbsolutePath()).thenReturn("/mydir");

    try {
      assertThat(mockFile, FileMatchers.isEmptyDirectory());
    }
    catch (AssertionError ae) {
      assertThat(ae.getMessage(), startsWith("\nExpected: an empty directory"
          + "\n     but: directory \"/mydir\" contained "
          + "\n\"foo.txt\""
          + "\n\"bar\""));
    }
  }

  @Test
  public void isEmptyDirectoryFailsOnIOExceptionReadingDirectoryList()
      throws IOException
  {
    // simulate IOException by trickery since file needs to directory but list() still return null
    // bypasses case where a 'file' was given instead of directory which would have also caused list() to return null
    when(mockFile.list()).thenReturn(null);
    when(mockFile.isDirectory()).thenReturn(true);
    // sugar
    when(mockFile.getAbsolutePath()).thenReturn("/mydir");

    try {
      assertThat(mockFile, FileMatchers.isEmptyDirectory());
    }
    catch (AssertionError ae) {
      assertThat(ae.getMessage(), startsWith(
          "\nExpected: an empty directory\n     but: there was an IO problem reading the contents of \"/mydir\""));
    }
  }


  @Test
  public void matchSha1()
      throws IOException
  {
    File sizedFile = new File(REAL_DIR, "src/test/resources/sized_file.txt");
    assertThat(sizedFile, FileMatchers.matchSha1("5ac7a73e644f48918b98531f9cd08a3e063b91a1"));
    assertThat(sizedFile, not(FileMatchers.matchSha1("123")));
  }

}
