/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.testsupport.port;

import org.sonatype.goodies.testsupport.TestSupport;

import com.google.common.collect.Range;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Tests for {@link PortRegistry}.
 */
public class PortRegistryTest
    extends TestSupport
{
  private PortRegistry portRegistry;

  @Before
  public void setUp() throws Exception {
    portRegistry = new PortRegistry();
  }

  @Test
  public void basicOperation() throws Exception {
    int port = portRegistry.reservePort();
    log(port);
    assertTrue(port > 0);
    portRegistry.releasePort(port);
  }

  @Test(expected = IllegalArgumentException.class)
  public void unreserveNonReservedPort() throws Exception {
    portRegistry.releasePort(9999);
  }

  @Test
  public void blockedSetOfPortsShouldNeverBeReservable() throws Exception {
    PortRegistry spied = new PortRegistry(0, 0, 100);
    spied.blockPorts(Sets.newSet(1));
    portRegistry = spy(spied);
    doReturn(1).when(portRegistry).findFreePort();
    assertCannotReservePort();

    // opposite
    portRegistry = spy(new PortRegistry());
    doReturn(1).when(portRegistry).findFreePort();
    assertReservePort(portRegistry, 1);
  }

  @Test
  public void blockedRangeOfPortsShouldNeverBeReservable() throws Exception {
    PortRegistry spied = new PortRegistry(0, 0, 100);
    spied.blockPorts(Range.closed(1,1));
    portRegistry = spy(spied);
    doReturn(1).when(portRegistry).findFreePort();
    assertCannotReservePort();

    // opposite
    portRegistry = spy(new PortRegistry());
    doReturn(1).when(portRegistry).findFreePort();
    assertReservePort(portRegistry, 1);
  }

  @Test
  public void addedBlockedRangeForPreviouslyUnsetRangeIsBlocked() throws Exception {
    portRegistry = spy(new PortRegistry(0, 0, 100));
    doReturn(1).when(portRegistry).findFreePort();
    portRegistry.blockPorts(Range.closed(1, 1));
    assertCannotReservePort();
  }

  @Test
  public void addedBlockedRangeIntersectingPreviouslyAddedRangeBlocksReservation() throws Exception {
    portRegistry = spy(new PortRegistry(0, 0, 100));
    portRegistry.blockPorts(Range.closed(2, 5));
    portRegistry.blockPorts(Range.closed(3, 7));

    // resulting range of blocked ports should be 3-5
    doReturn(4).when(portRegistry).findFreePort();

    assertCannotReservePort();
  }

  @Test
  public void addedBlockedRangeDisparateFromPreviouslyAddedRangeBlocksReservation() throws Exception {
    portRegistry = spy(new PortRegistry(0, 0, 100));
    portRegistry.blockPorts(Range.closed(2, 5));
    portRegistry.blockPorts(Range.closed(7, 10));

    doReturn(4).when(portRegistry).findFreePort();
    assertCannotReservePort();

    doReturn(8).when(portRegistry).findFreePort();
    assertCannotReservePort();

    doReturn(6).when(portRegistry).findFreePort();
    assertReservePort(portRegistry, 6);

  }

  @Test
  public void multipleRangesCanBlocked() throws Exception {
    portRegistry = spy(new PortRegistry(0, 0, 100));
    portRegistry.blockPorts(Range.closed(2, 5));
    portRegistry.blockPorts(Range.closed(7, 10));

    portRegistry.blockPorts(Range.closed(9, 12));

    doReturn(2).when(portRegistry).findFreePort();
    assertCannotReservePort();

    doReturn(7).when(portRegistry).findFreePort();
    assertCannotReservePort();

    doReturn(11).when(portRegistry).findFreePort();
    assertCannotReservePort();

    doReturn(1).when(portRegistry).findFreePort();
    assertReservePort(portRegistry, 1);

    doReturn(13).when(portRegistry).findFreePort();
    assertReservePort(portRegistry, 13);

  }

  @Test
  public void largeBlockedRangeCanBeSkippedOver() throws Exception {
    portRegistry = new PortRegistry(15000, 30000, 30 * 1000);
    portRegistry.blockPorts(Range.closed(15000, 16000));
    assertTrue(portRegistry.reservePort() > 16000);
  }

  private void assertCannotReservePort() {
    try {
      portRegistry.reservePort();
      assertThat("Expected a port to be blocked", true, is(false));
    }
    catch (RuntimeException re) {
      //expected
    }
  }

  private void assertReservePort(PortRegistry s, int port) {
    try {
      assertThat(s.reservePort(), is(port));
    }
    catch (RuntimeException re) {
      assertThat("Expected a port to be available", true, is(false));
    }
  }


}
