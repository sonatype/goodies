/**
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.sonatype.sisu.goodies.appevents;

import javax.inject.Inject;

/**
 * @since 1.5
 *
 * @deprecated Sisu should detect listeners if bound properly.
 */
@Deprecated
public abstract class AbstractApplicationEventListener
    implements EventListener
{
    @Inject
    public AbstractApplicationEventListener(final ApplicationEventMulticaster multicaster) {
        multicaster.addEventListener(this);
    }
}
