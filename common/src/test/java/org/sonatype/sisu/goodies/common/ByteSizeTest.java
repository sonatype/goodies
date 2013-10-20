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

import org.sonatype.sisu.goodies.common.ByteSize.ByteUnit;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

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
    assertThat(size, equalTo(ByteSize.megaBytes(100)));
  }

  @Test
  public void parse_n_M() throws Exception {
    ByteSize size = ByteSize.parse("100 m");
    log(size);
    assertThat(size, equalTo(ByteSize.megaBytes(100)));
  }

  @Test
  public void parse__n_M_() throws Exception {
    ByteSize size = ByteSize.parse(" 100 m ");
    log(size);
    assertThat(size, equalTo(ByteSize.megaBytes(100)));
  }

  @Test
  public void parse_nMB() throws Exception {
    ByteSize size = ByteSize.parse("100mb");
    log(size);
    assertThat(size, equalTo(ByteSize.megaBytes(100)));
  }

  @Test
  public void toKilosToBytes() throws Exception {
    ByteSize size = ByteSize.kiloBytes(2);
    log(size);
    assertThat(size.value(), equalTo((long) 2));
    assertThat(size.toKiloBytes(), equalTo((long) 2));
    assertThat(size.toBytes(), equalTo((long) 2048));
  }

  @Test
  public void bytes() throws Exception {
    ByteSize size = ByteSize.bytes(1);
    log(size);
    log("b: {}", size.toBytes());
    log("kb: {}", size.toKiloBytes());
    log("mb: {}", size.toMegaBytes());
    log("gb: {}", size.toGigaBytes());
    log("tb: {}", size.toTeraBytes());
  }

  @Test
  public void kilo() throws Exception {
    ByteSize size = ByteSize.kiloBytes(1);
    log(size);
    log("b: {}", size.toBytes());
    log("kb: {}", size.toKiloBytes());
    log("mb: {}", size.toMegaBytes());
    log("gb: {}", size.toGigaBytes());
    log("tb: {}", size.toTeraBytes());
  }

  @Test
  public void mega() throws Exception {
    ByteSize size = ByteSize.megaBytes(1);
    log(size);
    log("b: {}", size.toBytes());
    log("kb: {}", size.toKiloBytes());
    log("mb: {}", size.toMegaBytes());
    log("gb: {}", size.toGigaBytes());
    log("tb: {}", size.toTeraBytes());
  }

  @Test
  public void giga() throws Exception {
    ByteSize size = ByteSize.gigaBytes(1);
    log(size);
    log("b: {}", size.toBytes());
    log("kb: {}", size.toKiloBytes());
    log("mb: {}", size.toMegaBytes());
    log("gb: {}", size.toGigaBytes());
    log("tb: {}", size.toTeraBytes());
  }

  @Test
  public void tera() throws Exception {
    ByteSize size = ByteSize.teraBytes(1);
    log(size);
    log("b: {}", size.toBytes());
    log("kb: {}", size.toKiloBytes());
    log("mb: {}", size.toMegaBytes());
    log("gb: {}", size.toGigaBytes());
    log("tb: {}", size.toTeraBytes());
  }

  @Test
  public void asKiloBytes() {
    ByteSize size = ByteSize.bytes(1024).asKiloBytes();
    assertThat(size.unit(), equalTo(ByteUnit.KILOBYTES));
    assertThat(size.value(), equalTo(1L));
  }
}