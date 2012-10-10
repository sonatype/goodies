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

import com.google.common.collect.Maps;
import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link TemplateParameters}
 */
public class TemplateParametersTest
    extends TestSupport
{
    @Test
    public void empty() {
        Map<String, Object> params = new TemplateParameters().get();
        log(params);

        assertNotNull(params);
        assertThat(params.size(), is(0));
    }

    @Test
    public void mixedTypes() {
        Map<String, Object> params = new TemplateParameters()
            .set("a", "1")
            .set("b", 2)
            .get();
        log(params);

        assertNotNull(params);
        assertThat(params.size(), is(2));
        assertThat(params.get("a"), is((Object) "1"));
        assertThat(params.get("b"), is((Object) 2));
    }

    @Test
    public void setAll() {
        Map<String, Object> other = Maps.newHashMap();
        other.put("a", "1");
        other.put("b", 2);

        Map<String, Object> params = new TemplateParameters()
            .setAll(other)
            .get();
        log(params);

        assertNotNull(params);
        assertThat(params.size(), is(2));
        assertThat(params.get("a"), is((Object) "1"));
        assertThat(params.get("b"), is((Object) 2));
    }
}
