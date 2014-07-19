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
  /**
   * Start given lifecycle and propagate exceptions.
   */
  public static void start(final Lifecycle lifecycle) {
    checkNotNull(lifecycle);

    try {
      lifecycle.start();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Start given object if it implements {@link Lifecycle}.
   */
  public static void start(final Object lifecycle) {
    checkNotNull(lifecycle);

    if (lifecycle instanceof Lifecycle) {
      start((Lifecycle)lifecycle);
    }
  }

  /**
   * Stop given lifecycle and propagate exceptions.
   */
  public static void stop(final Lifecycle lifecycle) {
    checkNotNull(lifecycle);

    try {
      lifecycle.stop();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Stop given object if it implements {@link Lifecycle}.
   */
  public static void stop(final Object lifecycle) {
    checkNotNull(lifecycle);

    if (lifecycle instanceof Lifecycle) {
      stop((Lifecycle)lifecycle);
    }
  }
}
