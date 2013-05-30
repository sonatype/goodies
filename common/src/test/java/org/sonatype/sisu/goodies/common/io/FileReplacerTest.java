/*
 * Copyright (c) 2008-2013 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.common.io;

import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonatype.sisu.goodies.common.io.FileReplacer.ContentWriter;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers.exists;

/**
 * Tests for {@link FileReplacer}.
 */
public class FileReplacerTest
    extends TestSupport
{
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private FileReplacer fileReplacer;

    @Before
    public void setUp()
        throws Exception
    {
        File testFile = testFolder.newFile("test.txt");
        Files.append("initial", testFile, Charset.forName("UTF-8"));
        assertThat(readFirstLine(testFile), is("initial"));

        fileReplacer = new FileReplacer(testFile);
        fileReplacer.setDeleteBackupFile(true);

        log("Target File: {}", fileReplacer.getFile());
        log("Temp File: {}", fileReplacer.getTempFile());
        log("Backup File: {}", fileReplacer.getBackupFile());
    }

    private String readFirstLine(final File file) throws IOException {
        return Files.readFirstLine(file, Charset.forName("UTF-8"));
    }

    private void assertFileCount(final File dir, final int size) {
        File[] files = dir.listFiles();
        assertThat(files, notNullValue());
        assertThat(files.length, is(size));
    }

    @Test
    public void writeWithSuccessReplacesFile()
        throws Exception
    {
        fileReplacer.replace(new ContentWriter()
        {
            @Override
            public void write(final BufferedOutputStream output) throws IOException {
                output.write("hello".getBytes());
            }
        });

        // target file should exist and have the correct content
        assertThat(fileReplacer.getFile(), exists());
        assertThat(readFirstLine(fileReplacer.getFile()), is("hello"));

        // tmp and backup files should not exist
        assertThat(fileReplacer.getTempFile(), not(exists()));
        assertThat(fileReplacer.getBackupFile(), not(exists()));

        // sanity assert we are not leaking files
        assertFileCount(testFolder.getRoot(), 1);
    }

    @Test
    public void writeWithSuccessReplacesFileWithBackup()
        throws Exception
    {
        fileReplacer.setDeleteBackupFile(false);

        fileReplacer.replace(new ContentWriter()
        {
            @Override
            public void write(final BufferedOutputStream output) throws IOException {
                output.write("hello".getBytes());
            }
        });

        // target file should exist and have the correct content
        assertThat(fileReplacer.getFile(), exists());
        assertThat(readFirstLine(fileReplacer.getFile()), is("hello"));

        // tmp file should not exist
        assertThat(fileReplacer.getTempFile(), not(exists()));

        // backup file should exist with previous content
        assertThat(fileReplacer.getBackupFile(), exists());
        assertThat(readFirstLine(fileReplacer.getBackupFile()), is("initial"));

        // sanity assert we are not leaking files
        assertFileCount(testFolder.getRoot(), 2);
    }

    @Test
    public void writeWithErrorLeavesFile()
        throws Exception
    {
        try {
            fileReplacer.replace(new ContentWriter()
            {
                @Override
                public void write(final BufferedOutputStream output) throws IOException {
                    output.write("oops".getBytes());
                    output.flush();
                    throw new IOException("test failure");
                }
            });

            fail("replace() should have propagated exception");
        }
        catch (IOException e) {
            // expected
        }

        // target file should exist but unchanged
        assertThat(fileReplacer.getFile(), exists());
        assertThat(readFirstLine(fileReplacer.getFile()), is("initial"));

        // tmp file should exist with the partial written content
        assertThat(fileReplacer.getTempFile(), exists());
        assertThat(readFirstLine(fileReplacer.getTempFile()), is("oops"));

        // backup file should not exit
        assertThat(fileReplacer.getBackupFile(), not(exists()));

        // sanity assert we are not leaking files
        assertFileCount(testFolder.getRoot(), 2);
    }
}
