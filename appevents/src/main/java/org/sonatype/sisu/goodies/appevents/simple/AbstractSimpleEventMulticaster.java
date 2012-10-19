/**
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.sonatype.sisu.goodies.appevents.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.sisu.goodies.appevents.Event;
import org.sonatype.sisu.goodies.appevents.EventListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author cstamas
 * @since 1.5
 */
public abstract class AbstractSimpleEventMulticaster
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();

    public void addEventListener(final EventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(final EventListener listener) {
        listeners.remove(listener);
    }

    public void notifyEventListeners(final Event<?> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Notifying {} EventListener about event {} fired ({})", listeners.size(), event.getClass().getName(), event);
        }

        for (EventListener listener : listeners) {
            if (listener == null) continue;
            try {
                listener.onEvent(event);
            }
            catch (Exception e) {
                logger.info("Unexpected exception in listener {}, continuing listener notification.", listener.getClass().getName(), e);
            }
        }
    }
}
