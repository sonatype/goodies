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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.google.common.base.Throwables;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to read Maven pom.properties metadata.
 *
 * @since 1.9
 */
public class PomProperties
  extends Properties
{
  public static final String UNKNOWN = "unknown";

  public static final String GROUP_ID = "groupId";

  public static final String ARTIFACT_ID = "artifactId";

  public static final String VERSION = "version";

  private static final Logger log = Loggers.getLogger(PomProperties.class);

  public PomProperties(final Class owner, final String groupId, final String artifactId) {
    checkNotNull(owner);
    checkNotNull(groupId);
    checkNotNull(artifactId);
    try {
      loadMetadata(owner, groupId, artifactId);
    }
    catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private void loadMetadata(final Class owner, final String groupId, final String artifactId) throws IOException {
    String path = String.format("/META-INF/maven/%s/%s/pom.properties", groupId, artifactId);
    URL url = owner.getResource(path);
    if (url == null) {
      log.warn("Missing Maven pom.properties metadata: {}", path);
      return;
    }

    log.debug("Loading properties from: {}", url);
    InputStream input = url.openStream();
    try {
      this.load(input);
    }
    finally {
      input.close();
    }

    // Complain if there is a mismatch between what we expect the gav to be and what it really is
    String foundGroupId = getGroupId();
    if (!groupId.equals(foundGroupId)) {
      log.warn("Artifact groupId mismatch; expected: {}, found: {}", groupId, foundGroupId);
    }
    String foundArtifactId = getArtifactId();
    if (!artifactId.equals(foundArtifactId)) {
      log.warn("Artifact artifactId mismatch; expected: {}, found: {}", artifactId, foundArtifactId);
    }
  }

  public String getGroupId() {
    return getProperty(GROUP_ID, UNKNOWN);
  }

  public String getArtifactId() {
    return getProperty(ARTIFACT_ID, UNKNOWN);
  }

  public String getVersion() {
    return getProperty(VERSION, UNKNOWN);
  }
}
