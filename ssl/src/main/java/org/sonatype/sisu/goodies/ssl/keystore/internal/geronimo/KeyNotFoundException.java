/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.sonatype.sisu.goodies.ssl.keystore.internal.geronimo;

import org.sonatype.sisu.goodies.ssl.keystore.geronimo.KeystoreException;

/**
 * Exception indicating that the private key you tried to retrieve does not exist.
 *
 * @version $Rev$ $Date$
 */
public class KeyNotFoundException
    extends KeystoreException
{

    public KeyNotFoundException( String message )
    {
        super( message );
    }

    public KeyNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}