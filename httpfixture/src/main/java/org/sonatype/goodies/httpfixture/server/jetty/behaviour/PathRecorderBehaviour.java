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
package org.sonatype.goodies.httpfixture.server.jetty.behaviour;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.goodies.httpfixture.server.api.Behaviour;

/**
 * {@link Behaviour} implementation that records request path(s) (URIs) for processed HTTP verb.
 *
 * The URIs are in order for given HTTP verb, as they did income. This behaviour, while does similar thing as
 * {@link Record}, it differs from it that here, paths are reusable and stored as is (Record combines verb and path
 * and other info into one string), and this class does not reorder request paths, it keeps their "income" order
 * (Recorder reverts the list, last request becomes first).
 */
public class PathRecorderBehaviour
    extends BehaviourSupport
{
  // NOTE: Not using a synchronized Multimap to avoid artificially serializing concurrent clients 
  private final ConcurrentMap<String, Collection<String>> pathsMap = new ConcurrentHashMap<>();

  public boolean execute(HttpServletRequest request, HttpServletResponse response, Map<Object, Object> ctx)
      throws Exception
  {
    final String path = request.getRequestURI();
    final String verb = request.getMethod();
    Collection<String> paths = pathsMap.get(verb);
    if (paths == null) {
      paths = new ConcurrentLinkedQueue<>();
      Collection<String> existing = pathsMap.putIfAbsent(verb, paths);
      if (existing != null) {
        paths = existing;
      }
    }
    paths.add(path);
    return true;
  }

  public List<String> getPathsForVerb(final String verb) {
    Collection<String> paths = pathsMap.get(verb);
    return new ArrayList<String>(paths != null ? paths : Collections.<String> emptyList());
  }

  public void clear() {
    pathsMap.clear();
  }
}
