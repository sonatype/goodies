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
package org.sonatype.sisu.goodies.i18n;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * Provides simple access to i18n messages.
 *
 * @since 1.0
 */
public interface MessageSource
{
    /**
     * @throws ResourceNotFoundException
     */
    String getMessage(@NonNls String code);

    String getMessage(@NonNls String code, @Nullable String defaultValue);

    /**
     * @throws ResourceNotFoundException
     */
    String format(@NonNls String code, Object... args);
}
