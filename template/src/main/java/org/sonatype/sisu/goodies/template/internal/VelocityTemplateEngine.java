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
package org.sonatype.sisu.goodies.template.internal;

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
import org.sonatype.sisu.goodies.template.TemplateEngine;
import org.sonatype.sisu.goodies.template.TemplateParameters;
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
