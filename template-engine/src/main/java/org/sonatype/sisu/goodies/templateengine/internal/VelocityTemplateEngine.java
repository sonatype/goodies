/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.goodies.templateengine.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.io.Closer;
import org.sonatype.sisu.goodies.templateengine.TemplateEngine;
import org.sonatype.sisu.goodies.templateengine.TemplateParameters;
import org.sonatype.sisu.velocity.Velocity;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

/**
 * Velocity based implementation of {@link TemplateEngine}.
 *
 * @since 1.4
 */
@Named
@Singleton
public class VelocityTemplateEngine
    extends ComponentSupport
    implements TemplateEngine
{

    private final Velocity velocity;

    @Inject
    public VelocityTemplateEngine( final Velocity velocity )
    {
        this.velocity = checkNotNull( velocity );
    }

    public String render( final Object owner, final @NonNls URL template, @Nullable Map<String, Object> params )
    {
        checkNotNull( template );
        // params can be null

        log.trace( "Rendering template: {} w/params: {}", template, params );

        if ( params == null )
        {
            params = Maps.newHashMap();
        }

        Reader input = null;
        try
        {
            input = new InputStreamReader( template.openStream() );

            VelocityEngine engine = velocity.getEngine();
            params.put( "owner", owner ); //NON-NLS

            StringWriter buff = new StringWriter();
            engine.evaluate( new VelocityContext( params ), buff, template.getFile(), input ); //NON-NLS

            String result = buff.toString();
            log.trace( "Result: {}", result );

            return result;
        }
        catch ( Exception e )
        {
            throw Throwables.propagate( e );
        }
        finally
        {
            Closer.close( input );
        }
    }

    public String render( final Object owner, final URL template, TemplateParameters params )
    {
        checkNotNull( params );
        return render( owner, template, params.get() );
    }

    public String render( final Object owner, final @NonNls String template, @Nullable Map<String, Object> params )
    {
        checkNotNull( template );
        // params can be null

        log.trace( "Rendering template: {} w/params: {}", template, params );

        URL resource = owner.getClass().getResource( template );
        if ( resource == null )
        {
            log.warn( "Missing resource for template: {}; for owner: {}", template, owner.getClass().getName() );
            return null;
        }

        return render( owner, resource, params );
    }

    public String render( final Object owner, final @NonNls String template, TemplateParameters params )
    {
        checkNotNull( params );
        return render( owner, template, params.get() );
    }

}
