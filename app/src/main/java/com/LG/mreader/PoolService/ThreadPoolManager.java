package com.LG.mreader.PoolService;

import android.util.Log;

import java.util.concurrent.*;

/**
 * Centralized thread pool manager for background tasks (e.g. image downloading / preprocessing).
 * Provides bounded queue and CallerRunsPolicy for backpressure.
 */
public class ThreadPoolManager {
    private static final String TAG = "ThreadPoolManager";

    private static ThreadPoolManager instance;
    private final ThreadPoolExecutor executor;

    // Configuration constants — tune as needed
    private static final int CORE_THREADS = 3;           // minimum threads kept alive
    private static final int MAX_THREADS = 3;            // fixed pool size for stability
    private static final int QUEUE_CAPACITY = 70;        // how many pending tasks before throttling
    private static final long KEEP_ALIVE_SECONDS = 60L;  // unused here but harmless

    private ThreadPoolManager() {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        executor = new ThreadPoolExecutor(
                CORE_THREADS,
                MAX_THREADS,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                workQueue,
                new NamedThreadFactory("MReaderWorker"),
                new ThreadPoolExecutor.CallerRunsPolicy() // apply backpressure instead of OOM
        );

        executor.allowCoreThreadTimeOut(false);
        Log.d(TAG, "Initialized thread pool with capacity " + QUEUE_CAPACITY);
    }

    /** Singleton access */
    public static synchronized ThreadPoolManager getInstance() {
        if (instance == null) instance = new ThreadPoolManager();
        return instance;
    }

    /** Submit a background task (Runnable). */
    public void submitTask(Runnable task) {
        try {
            executor.execute(task);
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "Task rejected (queue full). Consider increasing capacity.", e);
        }
    }

    /** Submit a Callable task (returns a Future). */
    public <T> Future<T> submitTask(Callable<T> task) {
        try {
            return executor.submit(task);
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "Task rejected (queue full). Consider increasing capacity.", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /** Shut down gracefully (should be called on app close). */
    public void shutdown() {
        Log.d(TAG, "Shutting down thread pool...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                Log.w(TAG, "Forced thread pool shutdown.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /** Debug helper — prints status of the pool. */
    public void logStatus() {
        Log.d(TAG, String.format("Pool: active=%d, queued=%d, completed=%d",
                executor.getActiveCount(),
                executor.getQueue().size(),
                executor.getCompletedTaskCount()));
    }

    /** Lightweight custom ThreadFactory with named threads for easier debugging. */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String baseName;
        private int counter = 0;

        NamedThreadFactory(String baseName) {
            this.baseName = baseName;
        }

        @Override
        public synchronized Thread newThread(Runnable r) {
            Thread t = new Thread(r, baseName + "-" + (++counter));
            t.setPriority(Thread.NORM_PRIORITY);
            t.setDaemon(false);
            return t;
        }
    }
}
