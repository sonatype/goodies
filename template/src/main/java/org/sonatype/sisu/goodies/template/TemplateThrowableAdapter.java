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
package org.sonatype.sisu.goodies.template;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jetbrains.annotations.NonNls;

/**
 * Helper to deal with {@link Throwable} instances in a template.
 *
 * @since 1.4
 */
public class TemplateThrowableAdapter
{

    @NonNls
    public static final String NL = System.getProperty( "line.separator" );

    private final Throwable cause;

    public TemplateThrowableAdapter( final Throwable cause )
    {
        this.cause = checkNotNull( cause );
    }

    @TemplateAccessible
    public Throwable getCause()
    {
        return cause;
    }

    @TemplateAccessible
    public String getType()
    {
        return cause.getClass().getName();
    }

    @TemplateAccessible
    public String getSimpleType()
    {
        return cause.getClass().getSimpleName();
    }

    @TemplateAccessible
    public String getMessage()
    {
        return cause.getMessage();
    }

    @TemplateAccessible
    public String getTrace()
    {
        StringWriter buff = new StringWriter();
        cause.printStackTrace( new PrintWriter( buff ) );
        String tmp = buff.toString();
        return tmp.replace( NL, "<br/>" ); //NON-NLS
    }

    public String toString()
    {
        return cause.toString();
    }

}