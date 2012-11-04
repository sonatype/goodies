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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.guice.bean.locators.BeanLocator;
import org.sonatype.inject.BeanEntry;
import org.sonatype.inject.Mediator;
import org.sonatype.sisu.goodies.eventbus.EventBus;
import org.sonatype.sisu.goodies.eventbus.internal.guava.EventHandler;
import com.google.common.collect.Lists;
import com.google.inject.Key;

/**
 * {@link org.sonatype.sisu.goodies.eventbus.EventBus} implementation using guava event bus.
 * <p/>
 * It differs from guava event bus by dispatching events as they appear (is re-entrant). Guava will queue up all event
 * and dispatch them in the order they were posted, without re-entrance.
 *
 * @since 1.2
 */
@Named
@Singleton
public class DefaultEventBus
    implements EventBus
{

    private static final Logger LOG = LoggerFactory.getLogger( DefaultEventBus.class );

    private final org.sonatype.sisu.goodies.eventbus.internal.guava.EventBus eventBus;

    private final BeanLocator beanLocator;

    private final AtomicBoolean handlersLoaded;

    @Inject
    public DefaultEventBus( final BeanLocator beanLocator )
    {
        this.beanLocator = checkNotNull( beanLocator );
        eventBus = createEventBus();
        handlersLoaded = new AtomicBoolean( false );
    }

    @Override
    public EventBus register( final Object handler )
    {
        eventBus.register( handler );
        LOG.info( "Registered handler '{}'", handler );
        return this;
    }

    @Override
    public EventBus unregister( final Object handler )
    {
        eventBus.unregister( handler );
        LOG.debug( "Unregistered handler '{}'", handler );
        return this;
    }

    @Override
    public EventBus post( final Object event )
    {
        if ( handlersLoaded.compareAndSet( false, true ) )
        {
            registerHandlers( beanLocator );
        }
        LOG.debug( "Event '{}' fired", event );
        eventBus.post( event );
        return this;
    }

    private org.sonatype.sisu.goodies.eventbus.internal.guava.EventBus createEventBus()
    {
        return new org.sonatype.sisu.goodies.eventbus.internal.guava.EventBus( "default" )
        {
            /** List of events for the current thread to dispatch */
            private final ThreadLocal<List<EventWithHandler>> eventsToDispatch =
                new ThreadLocal<List<EventWithHandler>>();

            @Override
            protected void enqueueEvent( final Object event, final EventHandler handler )
            {
                if ( eventsToDispatch.get() == null )
                {
                    eventsToDispatch.set( Lists.<EventWithHandler>newArrayList() );
                }
                eventsToDispatch.get().add( new EventWithHandler( event, handler ) );
            }

            @Override
            protected void dispatchQueuedEvents()
            {
                final List<EventWithHandler> eventWithHandlers = eventsToDispatch.get();
                if ( eventWithHandlers != null )
                {
                    eventsToDispatch.remove();
                    for ( final EventWithHandler eventWithHandler : eventWithHandlers )
                    {
                        if ( LOG.isDebugEnabled() )
                        {
                            LOG.debug( "Dispatching '{}' to {}", eventWithHandler.event, eventWithHandler.handler );
                        }
                        dispatch( eventWithHandler.event, eventWithHandler.handler );
                    }
                }
            }
        };
    }

    private void registerHandlers( final BeanLocator beanLocator )
    {
        LOG.info( "Loading automatically registrable handlers" );
        beanLocator.watch(
            Key.get( Object.class ),
            new Mediator<Annotation, Object, DefaultEventBus>()
            {

                @Override
                public void add( final BeanEntry<Annotation, Object> entry, final DefaultEventBus watcher )
                    throws Exception
                {
                    if ( entry.getImplementationClass().isAnnotationPresent( EventBus.Managed.class ) )
                    {
                        register( entry.getValue() );
                    }
                }

                @Override
                public void remove( final BeanEntry<Annotation, Object> entry, final DefaultEventBus watcher )
                    throws Exception
                {
                    if ( entry.getImplementationClass().isAnnotationPresent( EventBus.Managed.class ) )
                    {
                        unregister( entry.getValue() );
                    }
                }
            },
            this
        );
    }

}
