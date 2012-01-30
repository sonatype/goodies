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
package org.sonatype.sisu.goodies.event.internal;

import org.junit.Test;
import org.sonatype.sisu.goodies.event.Event;
import org.sonatype.sisu.goodies.event.EventListener;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link EventBroadcasterImpl}.
 */
public class EventBroadcasterImplTest
    extends TestSupport
{
    @Test
    public void testBroadcast() {
        EventBroadcasterImpl<MyListener,Event> list = new EventBroadcasterImpl<MyListener,Event>()
        {
            @Override
            protected void broadcast(MyListener listener, Event event) throws Exception {
                listener.onEvent(event);
            }
        };

        MyListener listener1 = new MyListener();
        list.add(listener1);

        MyListener listener2 = new MyListener();
        list.add(listener2);

        Event event = new Event() {};
        list.broadcast(event);

        assertEquals(event, listener1.event);
        assertEquals(event, listener2.event);
    }

    private class MyListener
        implements EventListener
    {
        Object event;

        public void onEvent(Event event) {
            this.event = event;
        }
    }
}