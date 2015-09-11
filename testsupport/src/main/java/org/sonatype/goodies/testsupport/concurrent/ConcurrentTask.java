/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.testsupport.concurrent;

/**
 * A task of some sort to be executed concurrently with other tasks. Tasks must be stateless, thread-safe and
 * reusable.
 *
 * @since 1.15
 */
public interface ConcurrentTask
{
  /**
   * @throws Exception If the entire test should fail (e.g. in the case of a concurrency problem).
   */
  void run() throws Exception;
}
