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
package org.sonatype.sisu.goodies.common;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple reusable template based on newly introduced {@link SimpleFormat#format(String, Object...)} that defers
 * "calculation" until needed and is reusable. Usable when you need same template multiple times, for example to log a
 * message and then throw an exception with same message, but is not restricted to these cases.
 * <p>
 * With Guava:
 * 
 * <pre>
 * Preconditions.checkArgument( i &gt; 0, Template.of( &quot;The parameter i=%s is not positive!&quot;, i ) );
 * </pre>
 * 
 * The evaluation of Template will happen only when needed, thus, avoiding potentially unneeded
 * {@link String#format(String, Object...)} call. Next, a hokey-ish but workable solution with SLF4J to buy out Varargs:
 * 
 * <pre>
 * log.info("{}", Template.of("My %s", "format"))}
 * </pre>
 * 
 * Here also, you gain the fact that SLF4J with evaluate the template only if call is about to result in actual log (ie.
 * is "above" current logging level). Or something like:
 * 
 * <pre>
 * final Template t = Template.of( &quot;My %s message&quot;, &quot;customized&quot; );
 * log.debug( &quot;{}&quot;, t );
 * throw new Exception( t.toString() );
 * </pre>
 * 
 * Where you can reuse same message.
 * 
 * @author cstamas
 * @since 1.3
 */
public class Template
{
    private final StringBuilder format;

    private final ArrayList<Object> args;

    private Template()
    {
        this.format = new StringBuilder();
        this.args = new ArrayList<Object>();
    }

    private Template( final String format )
    {
        this();
        this.format.append( format );
    }

    public Template( final String format, final Object... args )
        throws IllegalArgumentException
    {
        this( format );
        this.args.addAll( Arrays.asList( args ) );
    }

    public String getFormat()
    {
        return format.toString();
    }

    public Object[] getArgs()
    {
        return args.toArray();
    }

    public Template appendFormat( final String format )
    {
        this.format.append( format );
        return this;
    }

    public Template appendArg( final Object arg )
    {
        args.add( arg );
        return appendFormat( SimpleFormat.PLACEHOLDER );
    }

    public String evaluate()
    {
        return SimpleFormat.format( getFormat(), getArgs() );
    }

    // ==

    @Override
    public String toString()
    {
        return evaluate();
    }

    // ==

    public static Template of( final String format, final Object... args )
    {
        return new Template( format, args );
    }

    public static Template of()
    {
        return new Template();
    }
}
