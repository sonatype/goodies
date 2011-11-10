/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.goodies.event.internal;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.event.Event;
import org.sonatype.sisu.goodies.event.EventBroadcaster;
import org.sonatype.sisu.goodies.event.EventListener;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link EventBroadcaster} implementation.
 *
 * @since 1.0
 */
public abstract class EventBroadcasterImpl<ListenerType extends EventListener, EventType extends Event>
    extends ComponentSupport
    implements EventBroadcaster<ListenerType,EventType>
{
    protected final List<ListenerType> listeners = new CopyOnWriteArrayList<ListenerType>();

    public void add(final ListenerType listener) {
        checkNotNull(listener);

        // don't allow duplicates this is really a set not a list
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void remove(final ListenerType listener) {
        checkNotNull(listener);
        listeners.remove(listener);
    }

    public Iterator<ListenerType> iterator() {
        return listeners.iterator();
    }

    public void broadcast(final EventType event) {
        log.debug("Broadcasting event: {}", event);

        for (ListenerType listener : listeners) {
            try {
                broadcast(listener, event);
            }
            catch (Exception e) {
                log.warn("Listener raised exception; ignoring", e);
            }
        }
    }

    protected abstract void broadcast(final ListenerType listener, final EventType event) throws Exception;
}
