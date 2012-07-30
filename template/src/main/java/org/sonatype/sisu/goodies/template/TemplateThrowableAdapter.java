/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.template;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jetbrains.annotations.NonNls;
import org.sonatype.sisu.goodies.common.ScriptAccessible;

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

    @ScriptAccessible
    public Throwable getCause()
    {
        return cause;
    }

    @ScriptAccessible
    public String getType()
    {
        return cause.getClass().getName();
    }

    @ScriptAccessible
    public String getSimpleType()
    {
        return cause.getClass().getSimpleName();
    }

    @ScriptAccessible
    public String getMessage()
    {
        return cause.getMessage();
    }

    @ScriptAccessible
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