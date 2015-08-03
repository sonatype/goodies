package org.sonatype.sisu.litmus.concurrenttest;

import java.util.concurrent.atomic.AtomicInteger;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the {@link ConcurrentRunner}
 */
public class ConcurrentRunnerTest
    extends TestSupport
{
  @Test
  public void tasksAreRunRepeatedly() throws Exception {
    final int iterations = 5;
    final ConcurrentRunner runner = new ConcurrentRunner(iterations, 10);

    final AtomicInteger runCount = new AtomicInteger(0);

    final int threads = 2;
    runner.addTask(threads, new ConcurrentTask()
    {
      @Override
      public void run() throws Exception {
        runCount.incrementAndGet();
      }
    });

    runner.go();

    final int expectedNumberOfRunInvocations = threads * iterations;
    assertThat(runCount.intValue(), is(expectedNumberOfRunInvocations));
    assertThat(runner.getRunInvocations(), is(expectedNumberOfRunInvocations));
  }

  @Test(expected = IllegalStateException.class)
  public void taskExceptionPropagatesOut() throws Exception {
    final ConcurrentRunner runner = new ConcurrentRunner(1, 10);

    runner.addTask(new ConcurrentTask()
    {
      @Override
      public void run() throws Exception {
        throw new IllegalStateException();
      }
    });

    runner.go();
  }

  @Test(expected = IllegalStateException.class)
  public void testTimesOut() throws Exception {
    // The test will time out unless the workers all end in less than 1 second
    final ConcurrentRunner runner = new ConcurrentRunner(1, 1);
    runner.addTask(1, new ConcurrentTask()
    {
      @Override
      public void run() throws Exception {
        // Sleep for 50 seconds, way past the timeout
        Thread.sleep(50 * 1000);
      }
    });

    runner.go();
  }
}
