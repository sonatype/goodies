/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.prefs.memory;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for {@link MemoryPreferences}.
 *
 * To configure set system property:
 *
 * {@code java.util.prefs.PreferencesFactory=org.sonatype.goodies.prefs.memory.MemoryPreferencesFactory}
 *
 * @since 1.5
 */
public class MemoryPreferencesFactory
    implements PreferencesFactory
{
  private static final Logger log = LoggerFactory.getLogger(MemoryPreferencesFactory.class);

  private MemoryPreferences system;

  private MemoryPreferences user;

  public synchronized Preferences systemRoot() {
    if (system == null) {
      system = new MemoryPreferences();
      log.debug("Created system root: {}", system);
    }
    return system;
  }

  public synchronized Preferences userRoot() {
    if (user == null) {
      user = new MemoryPreferences();
      log.debug("Created user root: {}", user);
    }
    return user;
  }
}