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

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import static com.google.common.base.Preconditions.checkArgument;
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

  private final int reservedPortMin;

  private final int reservedPortMax;

  private final int reservationTimeout;

  private int nextPort;

  public PortRegistry() {
    // The port range below is chosen to not overlap with the range typically used for ephemeral ports. This helps to
    // avoid situations where a previously reserved port ends being unavailable for its intended purpose because the OS
    // has reused the port to satisfy some other ephemeral port request in the meantime.
    this(10000, 30000, 60 * 1000);
  }

  /**
   * Creates a port registry that reserves ports within the specified range, failing after the given reservation timeout
   * if no free port has been found.
   * 
   * @since 2.2.3
   */
  public PortRegistry(final int reservedPortMin, final int reservedPortMax, final int reservationTimeout) {
    checkArgument(reservedPortMin >= 0 && reservedPortMin <= 65535);
    checkArgument(reservedPortMax >= 0 && reservedPortMax <= 65535);
    checkArgument(reservedPortMin <= reservedPortMax);
    checkArgument(reservationTimeout > 0);
    this.blockedPortRanges = Lists.newArrayList();
    this.blockedPorts = Sets.newHashSet();
    this.reservedPorts = Sets.newHashSet();
    this.reservedPortMin = reservedPortMin;
    this.reservedPortMax = reservedPortMax;
    this.reservationTimeout = reservationTimeout;
    nextPort = reservedPortMin;
  }

  /**
   * Returns a port that was 'free' and not explicitly blocked at the time of call
   *
   * @throws RuntimeException if a port could not be reserved
   */
  public synchronized int reservePort() {
    Exception error = null;
    for (long start = System.currentTimeMillis();;) {
      try {
        int port = findFreePort();
        if (!isBlocked(port) && reservedPorts.add(port)) {
          return port;
        }
      }
      catch (IOException e) {
        error = e;
      }
      if (System.currentTimeMillis() - start > reservationTimeout) {
        throw new IllegalStateException("Timed out trying to reserve port", error);
      }
    }
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
    for (Range<Integer> r : this.blockedPortRanges) {
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
  int findFreePort() throws IOException {
    if (reservedPortMin == 0) {
      return findFreePortAutomatic();
    }
    return findFreePortManual();
  }

  private int findFreePortAutomatic() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }

  private int findFreePortManual() throws IOException {
    for (int i = 0; i <= reservedPortMax - reservedPortMin; i++) {
      try (ServerSocket socket = new ServerSocket(nextPort)) {
        return socket.getLocalPort();
      }
      catch (BindException e) { // NOSONAR
        // port blocked, try the next one
      }
      finally {
        nextPort++;
        if (nextPort > reservedPortMax) {
          nextPort = reservedPortMin;
        }
      }
    }
    throw new BindException("No free port within range " + reservedPortMin + " to " + reservedPortMax);
  }
}
