/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ByteSize}.
 */
public class ByteSizeTest
    extends TestSupport
{
    @Test
    public void parse_nM() throws Exception {
        ByteSize size = ByteSize.parse("100m");
        log(size);
        assertThat(size, equalTo(ByteSize.megabytes(100)));
    }
    
    @Test
    public void toKilosToBytes() throws Exception {
        ByteSize kilos = ByteSize.kilobytes(2);
        assertThat(kilos.getValue(), equalTo((long)2));
        assertThat(kilos.toKiloBytes(), equalTo((long)2));
        assertThat(kilos.toBytes(), equalTo((long)2048));
    }
}