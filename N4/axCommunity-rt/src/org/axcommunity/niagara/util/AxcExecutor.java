package org.axcommunity.niagara.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Shared bounded executor for axCommunity background work (HTTP fetches, file
 * writes, BQL queries, DNS lookups). Replaces the module's ad-hoc raw
 * {@code new Thread()} sites: pool threads are named daemons, and when the
 * work queue is full the caller runs the task itself (back-pressure) instead
 * of the station piling up unbounded threads.
 *
 * <p>Periodic, self-rescheduling tasks should prefer {@link #tryExecute} so a
 * saturated pool drops one period instead of blocking the caller (which is
 * often the station clock thread).</p>
 *
 * @author S-Tier Building Automation
 */
public final class AxcExecutor
{
  private static final int POOL_SIZE = 4;
  private static final int QUEUE_CAPACITY = 32;

  private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
      POOL_SIZE, POOL_SIZE,
      0L, TimeUnit.MILLISECONDS,
      new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY),
      new ThreadFactory()
      {
        private int count = 0;
        public synchronized Thread newThread(Runnable r)
        {
          Thread t = new Thread(r, "axCommunity-worker-" + (++count));
          t.setDaemon(true);
          return t;
        }
      },
      new ThreadPoolExecutor.CallerRunsPolicy());

  private AxcExecutor() {}

  /**
   * Run a task on the shared pool. If the pool and queue are saturated the
   * CALLING thread runs the task (back-pressure) — never call this from the
   * station clock thread for blocking work; use {@link #tryExecute} there.
   */
  public static void execute(Runnable task)
  {
    POOL.execute(task);
  }

  /**
   * Try to run a task on the shared pool, returning false without running it
   * if the work queue is full. Use for periodic tasks where skipping one
   * period is safer than blocking the caller.
   */
  public static boolean tryExecute(Runnable task)
  {
    if (POOL.getQueue().remainingCapacity() == 0)
      return false;
    POOL.execute(task);
    return true;
  }
}
