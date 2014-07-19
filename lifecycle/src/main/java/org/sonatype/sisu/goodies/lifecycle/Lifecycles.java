package org.sonatype.sisu.goodies.lifecycle;

import com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Lifecycle} helpers.
 *
 * @since 1.10
 */
public class Lifecycles
{
  public static void start(final Lifecycle lifecycle) {
    checkNotNull(lifecycle);

    try {
      lifecycle.start();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static void start(final Object lifecycle) {
    checkNotNull(lifecycle);

    if (lifecycle instanceof Lifecycle) {
      start((Lifecycle)lifecycle);
    }
  }

  public static void stop(final Lifecycle lifecycle) {
    checkNotNull(lifecycle);

    try {
      lifecycle.stop();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static void stop(final Object lifecycle) {
    checkNotNull(lifecycle);

    if (lifecycle instanceof Lifecycle) {
      stop((Lifecycle)lifecycle);
    }
  }
}
