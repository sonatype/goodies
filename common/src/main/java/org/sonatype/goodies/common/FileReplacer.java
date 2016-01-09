/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Helper to facilitate writing to a temporary file and replacing target file with optional backup.
 *
 * @since 1.7
 */
public class FileReplacer
{
  private static final AtomicInteger counter = new AtomicInteger(0);

  private static final Logger log = Loggers.getLogger(FileReplacer.class);

  private final File file;

  private final String filePrefix;

  private final File tempFile;

  private final File backupFile;

  private boolean deleteBackupFile;

  public FileReplacer(final File file) {
    this.file = checkNotNull(file);

    // not using File.createTempFile() here so tmp + backup can share same timestamp-id
    // and delay creation of file until needed in the case of backup file
    // counter here just to ensure that sub-mills usage will not conflict

    this.filePrefix = file.getName() + "-" + System.currentTimeMillis() + "-" + counter.getAndIncrement();
    this.tempFile = new File(file.getParentFile(), filePrefix + ".tmp");
    this.backupFile = new File(file.getParentFile(), filePrefix + ".bak");
  }

  public FileReplacer(final String fileName) throws IOException {
    this(new File(checkNotNull(fileName)));
  }

  private void delete(final File file) throws IOException {
    boolean deleted = file.delete();
    if (!deleted) {
      throw new IOException("Failed to delete file: " + file);
    }
  }

  private void create(final File file) throws IOException {
    boolean created = file.createNewFile();
    if (!created) {
      throw new IOException("Failed to create file: " + file);
    }
  }

  public File getFile() {
    return file;
  }

  public File getTempFile() {
    return tempFile;
  }

  public File getBackupFile() {
    return backupFile;
  }

  public boolean isDeleteBackupFile() {
    return deleteBackupFile;
  }

  public void setDeleteBackupFile(final boolean deleteBackupFile) {
    this.deleteBackupFile = deleteBackupFile;
  }

  public interface ContentWriter
  {
    void write(final BufferedOutputStream output) throws IOException;
  }

  public void replace(final ContentWriter writer) throws IOException {
    checkNotNull(writer);

    // prepare directory structure
    file.getParentFile().mkdirs();

    // prepare temporary file
    if (tempFile.exists()) {
      log.warn("Temporary file already exists; removing: {}", tempFile);
      delete(tempFile);
    }
    create(tempFile);

    try {
      // delegate to do the write operation
      try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tempFile))) {
        writer.write(output);
      }
      catch (Exception e) {
        // complain with details about temp file and propagate exception
        log.warn("Failed to write temporary file: {}", tempFile, e);
        Throwables.propagateIfPossible(e, IOException.class);
        throw Throwables.propagate(e);
      }

      // replace the file only if operation succeeded
      replaceFile();
    }
    finally {
      if (tempFile.exists()) {
        delete(tempFile);
      }
    }
  }

  private void replaceFile() throws IOException {
    checkState(tempFile.exists(), "Temporary file missing");

    // backup target file if it exists
    if (file.exists()) {
      log.trace("Backing up target file: {} -> {}", file, backupFile);

      if (backupFile.exists()) {
        log.warn("Backup file already exists; removing: {}", backupFile);
        delete(backupFile);
      }

      Files.move(file, backupFile);
    }

    // move tmp file into place
    log.trace("Replacing file: {} -> {}", tempFile, file);
    Files.move(tempFile, file);

    // delete the backup file if requested
    if (backupFile.exists() && deleteBackupFile) {
      log.trace("Deleting backup file: {}", backupFile);
      delete(backupFile);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "file=" + file +
        ", filePrefix='" + filePrefix + '\'' +
        '}';
  }
}
