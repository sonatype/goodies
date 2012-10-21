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
package org.sonatype.sisu.goodies.eventbus.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.sisu.goodies.eventbus.EventBus;
import org.sonatype.sisu.litmus.testsupport.TestSupport;
import com.google.common.eventbus.Subscribe;

/**
 * TODO
 *
 * @since 1.5
 */
public class DefaultEventBusTest
    extends TestSupport
{

    @Test
    public void dispatchOrder()
    {
        final EventBus underTest = new DefaultEventBus();
        final Handler handler = new Handler( underTest );
        underTest.register( handler );
        underTest.post( "a string" );
        assertThat( handler.firstCalled, is( "handle2" ) );
    }

    private class Handler
    {

        private final EventBus eventBus;

        private String firstCalled = null;

        Handler( EventBus eventBus )
        {
            this.eventBus = eventBus;
        }

        @Subscribe
        public void handle1( String event )
        {
            eventBus.post( 1 );
            if ( firstCalled == null )
            {
                firstCalled = "handle1";
            }
        }

        @Subscribe
        public void handle2( Integer event )
        {
            if ( firstCalled == null )
            {
                firstCalled = "handle2";
            }
        }
    }

}
