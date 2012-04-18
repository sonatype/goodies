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
package org.sonatype.sisu.goodies.common;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit tests for {@link Template}.
 */
public class TemplateTest
{
    @Test
    public void simpleSanityTest()
    {
        final Template t = Template.of();
        assertThat( t, not( nullValue() ) );
        assertThat( t.evaluate(), equalTo( "" ) );

        t.appendFormat( "This is some format" );
        assertThat( t.evaluate(), equalTo( "This is some format" ) );

        t.appendArg( "FooBar" );
        assertThat( t.toString(), equalTo( "This is some formatFooBar" ) );
    }

    @Test
    public void simpleUseTest()
    {
        final Template t =
            Template.of( "The count is " ).appendArg( 1 ).appendFormat( " while foo=" ).appendArg( "bar" );
        assertThat( t, not( nullValue() ) );
        assertThat( t.evaluate(), equalTo( "The count is 1 while foo=bar" ) );
    }

    @Test
    public void constructor1()
    {
        final Template t = new Template( "%s - %s", "a", "b" );
        assertThat( t, not( nullValue() ) );
        assertThat( t.evaluate(), equalTo( "a - b" ) );
    }

    @Test
    public void constructor2()
    {
        final Template t = new Template( "%s - %s" );
        assertThat( t, not( nullValue() ) );
        assertThat( t.evaluate(), equalTo( "%s - %s" ) );
    }

    @Test
    public void constructor3()
    {
        final Template t = new Template( "%s - %s", "a", "b", "c" );
        assertThat( t, not( nullValue() ) );
        assertThat( t.evaluate(), equalTo( "a - b [c]" ) );
    }
}
