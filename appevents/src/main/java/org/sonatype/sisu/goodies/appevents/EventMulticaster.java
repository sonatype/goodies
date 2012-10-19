/**
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.sonatype.sisu.goodies.appevents;

/**
 * @since 1.5
 */
public interface EventMulticaster
{
    /**
     * Adds an event listener.
     *
     * @param listener the listener
     */
    void addEventListener(EventListener listener);

    /**
     * Removes an event listener.
     *
     * @param listener the listener
     */
    void removeEventListener(EventListener listener);

    /**
     * Notify event listeners.
     *
     * @param event the evt
     */
    void notifyEventListeners(Event<?> event);
}
