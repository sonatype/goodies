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
