/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.litmus.testsupport.port;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Act as a registry of reservedPorts available to use in the JVM.
 *
 * Ports which are 'free' and not explicitly blocked may be reserved for use.
 *
 * A free port is a port that this service can open a server socket with at the time of reservation.
 * A blocked port is either explicitly blocked or previously reserved.
 * There is no any kind of logic for "optimizing" the blocked port ranges and set at all, so a port might be
 * enlisted in both places as blocked.
 *
 * @since 1.12
 */
public class PortRegistry
{
  private static final int MAX_ATTEMPTS = 10;

  /**
   * Contiguous ranges of reservedPorts blocked.
   */
  private final List<Range<Integer>> blockedPortRanges;

  /**
   * Disparate set of reservedPorts blocked.
   */
  private final Set<Integer> blockedPorts;

  /**
   * Port registry, set of reserved ports.
   */
  private final Set<Integer> reservedPorts;

  public PortRegistry() {
    this.blockedPortRanges = Lists.newArrayList();
    this.blockedPorts = Sets.newHashSet();
    this.reservedPorts = Sets.newHashSet();
  }

  /**
   * Returns a port that was 'free' and not explicitly blocked at the time of call
   *
   * @throws RuntimeException if a port could not be reserved
   */
  public synchronized int reservePort() {
    int port = 0;
    int attempts = 0;
    boolean searchingForPort = true;
    while (searchingForPort && ++attempts < MAX_ATTEMPTS) {
      port = findFreePort();
      searchingForPort = isBlocked(port) || !reservedPorts.add(port);
    }
    if (searchingForPort) {
      throw new RuntimeException("Could not allocate a free port after " + MAX_ATTEMPTS + " attempts.");
    }
    return port;
  }

  /**
   * Releases a port that was reserved with {@link #releasePort(int)} call.
   *
   * @throws IllegalArgumentException if a port was not reserved in the first place.
   */
  public synchronized void releasePort(int port) {
    if (!reservedPorts.remove(port)) {
      throw new IllegalArgumentException("port " + port + " not yet reserved by this service.");
    }
  }

  public synchronized void blockPorts(final Range<Integer> blockedRange) {
    checkNotNull(blockedRange);
    this.blockedPortRanges.add(blockedRange);
  }

  public synchronized void blockPorts(final Set<Integer> blockedPorts) {
    checkNotNull(blockedPorts);
    this.blockedPorts.addAll(blockedPorts);
  }

  public synchronized void blockPorts(final int... blockedPorts) {
    this.blockedPorts.addAll(Ints.asList(blockedPorts));
  }

  /**
   * Returns {@code true} if port is blocked, should not be reserved.
   */
  private boolean isBlocked(int port) {
    for (Range r : this.blockedPortRanges) {
      if (r.contains(port)) {
        return true;
      }
    }
    return blockedPorts.contains(port);
  }

  /**
   * Find a random free system port.
   *
   * @return a free system port at the time this method was called.
   */
  @VisibleForTesting
  protected int findFreePort() {
    ServerSocket server;
    try {
      server = new ServerSocket(0);
    }
    catch (IOException e) {
      throw Throwables.propagate(e);
    }

    Integer portNumber = server.getLocalPort();
    try {
      server.close();
    }
    catch (IOException e) {
      throw new RuntimeException("Unable to release port " + portNumber, e);
    }
    return portNumber;
  }
}
