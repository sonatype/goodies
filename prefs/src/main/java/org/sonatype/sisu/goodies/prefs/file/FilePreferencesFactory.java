/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
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

package org.sonatype.sisu.goodies.prefs.file;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME: Update configuration property muck

// FIXME: Avoid usage of this class, until is ready for use...

/**
 * PreferencesFactory implementation that stores the preferences in a user-defined file.
 *
 * To use it, set the system property <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory</tt>.
 *
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the system property
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory.file</tt>.
 *
 * Based on <a href="http://www.davidc.net/programming/java/java-preferences-using-file-backing-store">
 * Java Preferences using a file as the backing store</a>.
 *
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @since 1.5
 * @deprecated Marked as deprecated to help avoid folks using until is been updated/test/etc
 */
//@Beta
@Deprecated
public class FilePreferencesFactory
    implements PreferencesFactory
{
  public static final String SYSTEM_PROPERTY_FILE = "net.infotrek.util.prefs.FilePreferencesFactory.file";

  private static final Logger log = LoggerFactory.getLogger(FilePreferencesFactory.class);

  private Preferences rootPreferences;

  public Preferences systemRoot() {
    return userRoot();
  }

  public Preferences userRoot() {
    if (rootPreferences == null) {
      log.debug("Instantiating root preferences");

      rootPreferences = new FilePreferences(null, "");
    }

    return rootPreferences;
  }

  private static File preferencesFile;

  public static File getPreferencesFile() {
    if (preferencesFile == null) {
      String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);

      if (prefsFile == null || prefsFile.length() == 0) {
        prefsFile = System.getProperty("user.home") + File.separator + ".fileprefs";
      }

      preferencesFile = new File(prefsFile).getAbsoluteFile();

      log.info("Preferences file: {}", preferencesFile);
    }

    return preferencesFile;
  }

  public static void setPreferencesFile(final File file) {
    preferencesFile = file;
  }
}