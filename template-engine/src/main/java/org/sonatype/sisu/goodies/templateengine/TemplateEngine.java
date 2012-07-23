/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.goodies.templateengine;

import java.net.URL;
import java.util.Map;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * Provides template rendering functionality.
 *
 * @since 1.4
 */
public interface TemplateEngine
{

    String render( Object owner, URL template, @Nullable Map<String, Object> params );

    String render( Object owner, URL template, TemplateParameters params );

    String render( Object owner, @NonNls String template, @Nullable Map<String, Object> params );

    String render( Object owner, @NonNls String template, TemplateParameters params );

}
