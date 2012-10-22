package org.sonatype.sisu.goodies.prefs.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * Factory for {@link MemoryPreferences}.
 *
 * To configure set system property:
 *
 * {@code java.util.prefs.PreferencesFactory=org.sonatype.sisu.goodies.prefs.memory.MemoryPreferencesFactory}
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