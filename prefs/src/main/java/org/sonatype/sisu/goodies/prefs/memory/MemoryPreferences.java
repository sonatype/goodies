package org.sonatype.sisu.goodies.prefs.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.sisu.goodies.prefs.file.FilePreferencesFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Transient in-memory-only {@link Preferences}.
 *
 * @since 1.5
 */
public class MemoryPreferences
    extends AbstractPreferences
{
    private static final Logger log = LoggerFactory.getLogger(MemoryPreferences.class);

    private final Map<String, String> root;

    private final Map<String, MemoryPreferences> children;

    public MemoryPreferences(final AbstractPreferences parent, final String name) {
        super(parent, name);
        root = new TreeMap<String, String>();
        children = new TreeMap<String, MemoryPreferences>();
    }

    public MemoryPreferences() {
        this(null, "");
    }

    @Override
    protected void putSpi(final String key, final String value) {
        root.put(key, value);
    }

    @Override
    protected String getSpi(final String key) {
        return root.get(key);
    }

    @Override
    protected void removeSpi(final String key) {
        root.remove(key);
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        // nop
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        return root.keySet().toArray(new String[root.keySet().size()]);
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
        // nop
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
        // nop
    }
}