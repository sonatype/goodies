/**
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.sonatype.sisu.goodies.appevents.simple;

import org.sonatype.sisu.goodies.appevents.ApplicationEventMulticaster;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author cstamas
 *
 * @since 1.5
 */
@Named
@Singleton
public class SimpleApplicationEventMulticaster
    extends AbstractSimpleEventMulticaster
    implements ApplicationEventMulticaster
{
    // empty
}
