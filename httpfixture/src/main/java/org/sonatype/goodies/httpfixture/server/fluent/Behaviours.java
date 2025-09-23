/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.httpfixture.server.fluent;

import java.io.File;

import org.sonatype.goodies.common.Time;
import org.sonatype.goodies.httpfixture.server.api.Behaviour;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.Content;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.ErrorBehaviour;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.Pause;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.Record;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.Redirect;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.filesystem.Get;

/**
 * Helper to create specific {@link Behaviour} instances.
 */
public class Behaviours
{
  public static Redirect redirect(String url)
  {
    return Redirect.redirect(url);
  }

  public static Redirect redirect(String url, int status)
  {
    return Redirect.redirect(url, status);
  }

  public static ErrorBehaviour error(int code)
  {
    return ErrorBehaviour.error(code);
  }

  public static ErrorBehaviour error(int code, String msg)
  {
    return ErrorBehaviour.error(code, msg);
  }

  public static Pause pause(Time time)
  {
    return Pause.pause(time);
  }

  public static Content content(byte[] content)
  {
    return Content.content(content);
  }

  public static Content content(byte[] content, String type)
  {
    return Content.content(content, type);
  }


  public static Content content(String content)
  {
    return Content.content(content);
  }

  public static Content content(String content, String type)
  {
    return Content.content(content, type);
  }

  public static Get get(File root)
  {
    return Get.get(root);
  }

  public static Content file(File content)
  {
    return new Content(content);
  }

  public static Content file(File content, String type)
  {
    return new Content(content, type);
  }

  public static Record record()
  {
    return new Record();
  }
}
