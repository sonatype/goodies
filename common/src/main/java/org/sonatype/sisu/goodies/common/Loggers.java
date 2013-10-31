package org.sonatype.sisu.goodies.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logger utilities.
 *
 * @since 1.7.3
 */
public final class Loggers
{
  private static final String GUICE_ENHANCED = "$$EnhancerByGuice$$";

  private static boolean isEnhancedSubclass(final Class type) {
    return type.getName().contains(GUICE_ENHANCED);
  }

  /**
   * Helper to create a logger and deal with class-names created by AOP platforms.
   */
  public static Logger getLogger(final Class type) {
    checkNotNull(type);
    if (isEnhancedSubclass(type)) {
      return LoggerFactory.getLogger(type.getSuperclass());
    }
    return LoggerFactory.getLogger(type);
  }

  public static Logger getLogger(final Object obj) {
    checkNotNull(obj);
    return getLogger(obj.getClass());
  }

  public static Logger getLogger(final String name) {
    checkNotNull(name);
    return LoggerFactory.getLogger(name);
  }
}
