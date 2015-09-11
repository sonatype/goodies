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

import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transient in-memory-only {@link Preferences}.
 *
 * @since 1.5
 */
public class MemoryPreferences
    extends AbstractPreferences
{
  private static final Logger log = LoggerFactory.getLogger(MemoryPreferences.class);

  public static final String ROOT_NAME = "";

  private final Map<String, String> entries;

  private final Map<String, MemoryPreferences> children;

  public MemoryPreferences(final AbstractPreferences parent, final String name) {
    super(parent, name);
    entries = new TreeMap<String, String>();
    children = new TreeMap<String, MemoryPreferences>();
  }

  public MemoryPreferences() {
    this(null, ROOT_NAME);
  }

  @Override
  protected void putSpi(final String key, final String value) {
    log.trace("Put: {}={}", key, value);
    entries.put(key, value);
  }

  @Override
  protected String getSpi(final String key) {
    log.trace("Get: {}", key);
    return entries.get(key);
  }

  @Override
  protected void removeSpi(final String key) {
    log.trace("Remove: {}", key);
    entries.remove(key);
  }

  @Override
  protected void removeNodeSpi() throws BackingStoreException {
    log.trace("Remove node");
    // nop
  }

  @Override
  protected String[] keysSpi() throws BackingStoreException {
    return entries.keySet().toArray(new String[entries.keySet().size()]);
  }

  @Override
  protected String[] childrenNamesSpi() throws BackingStoreException {
    return children.keySet().toArray(new String[children.keySet().size()]);
  }

  @Override
  protected MemoryPreferences childSpi(final String name) {
    MemoryPreferences child = children.get(name);
    if (child == null || child.isRemoved()) {
      child = new MemoryPreferences(this, name);
      children.put(name, child);
    }
    return child;
  }

  @Override
  protected void syncSpi() throws BackingStoreException {
    log.trace("Sync");
    // nop
  }

  @Override
  protected void flushSpi() throws BackingStoreException {
    log.trace("Flush");
    // nop
  }
}