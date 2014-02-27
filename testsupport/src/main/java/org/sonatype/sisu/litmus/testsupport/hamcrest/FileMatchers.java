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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

/**
 * Some ideas copied freely from http://www.time4tea.net/wiki/display/MAIN/Testing+Files+with+Hamcrest
 * <p/>
 * Converted to pure Hamcrest
 *
 * @author time4tea technology ltd 2007
 * @author plynch
 */
public class FileMatchers
{

  @Factory
  public static Matcher<File> isDirectory() {
    return new TypeSafeMatcher<File>()
    {
      File fileTested;

      @Override
      public boolean matchesSafely(File item) {
        fileTested = item;
        return item.isDirectory();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("that ");
        description.appendValue(fileTested);
        description.appendText(" is a directory");
      }

      @Override
      protected void describeMismatchSafely(File item, Description mismatchDescription) {
        if (item.isFile()) {
          mismatchDescription.appendText("is a file");
        }
        else {
          mismatchDescription.appendText("is neither a file or directory");
        }
      }

    };
  }

  @Factory
  public static Matcher<File> exists() {
    return new TypeSafeMatcher<File>()
    {
      File fileTested;

      @Override
      public boolean matchesSafely(File item) {
        fileTested = item;
        return item.exists();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("file ");
        description.appendValue(fileTested);
        description.appendText(" exists");
      }

      @Override
      protected void describeMismatchSafely(File item, Description mismatchDescription) {
        mismatchDescription.appendText("did not exist");
      }

    };
  }

  @Factory
  public static Matcher<File> isFile() {
    return new TypeSafeMatcher<File>()
    {
      File fileTested;

      @Override
      public boolean matchesSafely(File item) {
        fileTested = item;
        return item.isFile();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("that ");
        description.appendValue(fileTested);
        description.appendText(" is a file");
      }

      @Override
      protected void describeMismatchSafely(File item, Description mismatchDescription) {
        super.describeMismatchSafely(item, mismatchDescription);
        if (item.isDirectory()) {
          mismatchDescription.appendText("is a directory");
        }
        else {
          mismatchDescription.appendText("is neither a file or directory");
        }
      }

    };
  }

  @Factory
  public static Matcher<File> readable() {
    return new TypeSafeMatcher<File>()
    {
      File fileTested;

      @Override
      public boolean matchesSafely(File item) {
        fileTested = item;
        return item.canRead();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText(" that file ");
        description.appendValue(fileTested);
        description.appendText(" is readable");
      }

      @Override
      protected void describeMismatchSafely(File item, Description mismatchDescription) {
        mismatchDescription.appendText("not");
      }

    };
  }

  @Factory
  public static Matcher<File> writable() {
    return new TypeSafeMatcher<File>()
    {
      File fileTested;

      @Override
      public boolean matchesSafely(File item) {
        fileTested = item;
        return item.canWrite();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText(" that file ");
        description.appendValue(fileTested);
        description.appendText("is writable");
      }

      @Override
      protected void describeMismatchSafely(File item, Description mismatchDescription) {
        mismatchDescription.appendText("not");
      }
    };
  }

  @Factory
  public static Matcher<File> sized(Long size) {
    return sized(Matchers.equalTo(size));
  }

  @Factory
  public static Matcher<File> sized(final Matcher<Long> size) {
    return new TypeSafeMatcher<File>()
    {
      File fileTested;

      long actualLength;

      @Override
      public boolean matchesSafely(File item) {
        fileTested = item;
        actualLength = item.length();
        return size.matches(actualLength);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText(" a file ");
        description.appendValue(fileTested);
        description.appendText(" sized with ");
        description.appendDescriptionOf(size);
        description.appendText(" bytes");

      }

      @Override
      protected void describeMismatchSafely(File item, Description mismatchDescription) {
        mismatchDescription.appendText("was ");
        mismatchDescription.appendValue(actualLength);
        mismatchDescription.appendText(" bytes");

      }
    };
  }

  @Factory
  public static Matcher<File> named(final String name) {
    return new TypeSafeMatcher<File>()
    {
      private String filename;

      @Override
      public boolean matchesSafely(File item) {
        filename = item.getName();
        return name.matches(filename);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("File named ");
        description.appendValue(name);
      }

      @Override
      protected void describeMismatchSafely(File item, Description mismatchDescription) {
        mismatchDescription.appendText("named ");
        mismatchDescription.appendValue(filename);
      }

    };
  }

  @Factory
  public static Matcher<File> withCanonicalPath(final String path) {
    return new TypeSafeMatcher<File>()
    {

      private String canonPath;

      @Override
      public boolean matchesSafely(File item) {
        try {
          canonPath = item.getCanonicalPath();
          return path.matches(normalizePath(canonPath));
        }
        catch (IOException e) {
          return false;
        }
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("File with canonical path ");
        description.appendValue(path);
      }

      @Override
      protected void describeMismatchSafely(File item, Description description) {
        description.appendText("was ");
        description.appendValue(canonPath);
      }
    };
  }

  @Factory
  public static Matcher<File> withAbsolutePath(final String path) {
    return new TypeSafeMatcher<File>()
    {
      private String absPath;

      @Override
      public boolean matchesSafely(File item) {
        absPath = item.getAbsolutePath();
        return path.matches(normalizePath(absPath));
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("File with absolute path ");
        description.appendValue(path);
      }

      @Override
      protected void describeMismatchSafely(File item, Description description) {
        description.appendText("was ");
        description.appendValue(absPath);
      }

    };
  }

  @Factory
  public static Matcher<File> contains(final String... entries) {
    return new TypeSafeMatcher<File>()
    {

      private File item;

      private List<String> missing = new ArrayList<String>();

      @Override
      public boolean matchesSafely(File item) {
        this.item = item;
        try {
          String content = readFully(item);
          for (String entry : entries) {
            if (!content.contains(entry)) {
              missing.add(entry);
            }
          }
          return missing.isEmpty();
        }
        catch (IOException e) {
          return false;
        }
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("File ");
        description.appendValue(item);
        description.appendText(" contains ");
        description.appendValueList("[", "][", "]", entries);
      }

      @Override
      protected void describeMismatchSafely(File item, Description description) {
        description.appendText("did not contain ");
        description.appendValueList("[", "][", "]", missing);
      }

    };
  }

  /**
   * Assert a file contains only the specified string content
   */
  @Factory
  public static Matcher<File> containsOnly(final String content) {

    return new TypeSafeMatcher<File>()
    {

      private File item;

      private String fileContent;

      @Override
      public boolean matchesSafely(File item) {
        this.item = item;
        try {
          fileContent = readFully(item);
          return fileContent.equals(content);
        }
        catch (IOException e) {
          return false;
        }
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("File ");
        description.appendValue(item);
        description.appendText(" to contain only ");
        description.appendValue(content);
      }

      @Override
      protected void describeMismatchSafely(File item, Description description) {
        final String diff = DiffUtils.diffSideBySide(fileContent, content, true);
        description.appendText(diff);
      }

    };
  }

  @Factory
  public static Matcher<File> doesNotContain(final String... entries) {
    return new TypeSafeMatcher<File>()
    {

      private File item;

      private List<String> contained = new ArrayList<String>();

      @Override
      public boolean matchesSafely(File item) {
        this.item = item;
        try {
          String content = readFully(item);
          for (String entry : entries) {
            if (content.contains(entry)) {
              contained.add(entry);
            }
          }
          return contained.isEmpty();
        }
        catch (IOException e) {
          return false;
        }
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("File ");
        description.appendValue(item);
        description.appendText(" did not contain ");
        description.appendValueList("[", "][", "]", entries);
      }

      @Override
      protected void describeMismatchSafely(File item, Description description) {
        description.appendText("contained ");
        description.appendValueList("[", "][", "]", contained);
      }

    };
  }

  public static Matcher<ZipFile> containsEntry(final String entryName) {
    return new TypeSafeMatcher<ZipFile>()
    {

      @Override
      protected boolean matchesSafely(ZipFile item) {
        Enumeration<? extends ZipEntry> entries = item.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (entry.getName().equals(entryName)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("zip archive contains entry named ");
        description.appendValue(entryName);
      }

      @Override
      protected void describeMismatchSafely(ZipFile item, Description mismatchDescription) {
        mismatchDescription.appendText(" archive contained these entries:\n");
        Enumeration<? extends ZipEntry> entries = item.entries();
        while (entries.hasMoreElements()) {
          mismatchDescription.appendValue(entries.nextElement().getName());
          if (entries.hasMoreElements()) {
            mismatchDescription.appendText("\n");
          }
        }

      }
    };

  }

  /**
   * Read from file till EOF. FIXME: why is this here? move to util class, this class for Matchers only
   *
   * @param file the file from which to read
   * @return the contents read out of the given file
   * @throws IOException if the contents could not be read out from the reader.
   */
  public static String readFully(File file)
      throws IOException
  {
    Reader rdr = null;
    try {
      rdr = new BufferedReader(new FileReader(file));
      return readFully(rdr, 8192);
    }
    finally {
      if (rdr != null) {
        rdr.close();
      }
    }
  }

  /**
   * Read from reader till EOF. FIXME: why is this here? move to util class, this class for Matchers only
   *
   * @param rdr the reader from which to read.
   * @return the contents read out of the given reader.
   * @throws IOException if the contents could not be read out from the reader.
   */
  public static String readFully(Reader rdr)
      throws IOException
  {
    return readFully(rdr, 8192);
  }

  /**
   * Read from reader till EOF. FIXME: why is this here? move to util class, this class for Matchers only
   *
   * @param rdr        the reader from which to read.
   * @param bufferSize the buffer size to use when reading.
   * @return the contents read out of the given reader.
   * @throws IOException if the contents could not be read out from the reader.
   */
  public static String readFully(Reader rdr, int bufferSize)
      throws IOException
  {
    if (bufferSize <= 0) {
      throw new IllegalArgumentException("Buffer size must be greater " + "than 0");
    }
    final char[] buffer = new char[bufferSize];
    int bufferLength = 0;
    StringBuffer textBuffer = null;
    while (bufferLength != -1) {
      bufferLength = rdr.read(buffer);
      if (bufferLength > 0) {
        textBuffer = (textBuffer == null) ? new StringBuffer() : textBuffer;
        textBuffer.append(new String(buffer, 0, bufferLength));
      }
    }
    return (textBuffer == null) ? null : textBuffer.toString();
  }

  /**
   * @deprecated Use better named and more robust {@link #isEmptyDirectory()} instead. This will be removed in next
   *             version.
   */
  @Deprecated
  public static Matcher<? super File> isEmpty() {
    return isEmptyDirectory();
  }

  /**
   * Will only assert true if checking a directory, that directory's contents can be listed, and the directory is in
   * fact empty.
   *
   * @return a matcher that checks if a File (directory) is empty!
   * @since 1.3
   */
  public static Matcher<? super File> isEmptyDirectory() {
    return new TypeSafeMatcher<File>()
    {
      private File dir;

      private String[] files;

      public boolean matchesSafely(final File item) {
        this.dir = item;
        this.files = item.list();
        return files != null && files.length == 0;
      }

      public void describeTo(final Description description) {
        if (!dir.isDirectory()) {
          description.appendText("a directory");
        }
        else {
          description.appendText("an empty directory");
        }
      }

      @Override
      protected void describeMismatchSafely(final File item, final Description mismatchDescription) {
        if (!this.dir.isDirectory()) {
          mismatchDescription.appendText("found a non directory at ")
              .appendValue(this.dir.getAbsolutePath());
        }
        else if (this.files == null) {
          mismatchDescription.appendText("there was an IO problem reading the contents of ")
              .appendValue(this.dir.getAbsolutePath());
        }
        else {
          mismatchDescription.appendText("directory ").appendValue(this.dir.getAbsolutePath())
              .appendText(" contained ").appendValueList("\n", "\n", "", this.files);
        }
      }
    };
  }

  /**
   * @since 1.2
   */
  @Factory
  public static Matcher<? super File> matchSha1(File file)
      throws IOException
  {
    return matchSha1(createSHA1FromStream(file));
  }

  @Factory
  public static Matcher<? super File> matchSha1(final String expectedSha1) {
    return new TypeSafeMatcher<File>()
    {
      String sha1;

      File file;

      public boolean matchesSafely(File item) {
        this.file = item;
        try {
          sha1 = createSHA1FromStream(item);
        }
        catch (IOException e) {
          return false;
        }
        return sha1.equals(expectedSha1);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("File ");
        if (file != null) {
          description.appendValue(file);
        }
        description.appendText("with sha1 ");
        description.appendValue(expectedSha1);
      }

      @Override
      protected void describeMismatchSafely(File file, Description description) {
        description.appendText("File ");
        if (file != null) {
          description.appendValue(file);
        }
        description.appendText("was ");
        description.appendValue(sha1);
      }
    };
  }

  private static String createSHA1FromStream(File file)
      throws IOException
  {
    InputStream in = null;

    try {
      in = new FileInputStream(file);

      byte[] bytes = new byte[4096];
      MessageDigest digest;
      try {
        digest = MessageDigest.getInstance("SHA1");
      }
      catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException(e.getMessage(), e);
      }

      for (int n; (n = in.read(bytes)) >= 0; ) {
        if (n > 0) {
          digest.update(bytes, 0, n);
        }
      }

      bytes = digest.digest();
      StringBuffer sb = new StringBuffer(bytes.length * 2);
      for (int i = 0; i < bytes.length; i++) {
        int n = bytes[i] & 0xFF;
        if (n < 0x10) {
          sb.append('0');
        }
        sb.append(Integer.toHexString(n));
      }

      return sb.toString();
    }
    finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * Escape window separator char in order to user regex
   */
  public static String normalizePath(String pattern) {
    // windows escape separator
    if (File.separatorChar == '\\') {
      pattern = pattern.replace("\\", "\\\\");
    }
    return pattern;
  }

}
