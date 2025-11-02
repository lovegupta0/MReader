package com.LG.mreader.PoolService;

import android.util.Log;

import com.LG.mreader.Utility.ThreadsPoolManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CentralThreadPool implements ThreadsPoolManager {
    private static final String TAG = "ThreadPoolManager";

    private static CentralThreadPool instance;
    private final ThreadPoolExecutor executor;

    // Configuration constants — tune as needed
    private static final int CORE_THREADS = 2;           // minimum threads kept alive
    private static final int MAX_THREADS = 2;            // fixed pool size for stability
    private static final int QUEUE_CAPACITY = 70;        // how many pending tasks before throttling
    private static final long KEEP_ALIVE_SECONDS = 60L;
    private CentralThreadPool(){
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        executor = new ThreadPoolExecutor(
                CORE_THREADS,
                MAX_THREADS,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                workQueue,
                new CentralThreadPool.NamedThreadFactory("CenteralReaderWorker"),
                new ThreadPoolExecutor.CallerRunsPolicy() // apply backpressure instead of OOM
        );

        executor.allowCoreThreadTimeOut(false);
        Log.d(TAG, "Initialized thread pool with capacity " + QUEUE_CAPACITY);
    }
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
    public static CentralThreadPool getInstance(){
        if(instance==null){
            synchronized (CentralThreadPool.class){
                if(instance==null){
                    instance=new CentralThreadPool();
                }
            }
        }
        return instance;
    }
    @Override
    public void submitTask(Runnable task) {
        try {
            executor.execute(task);
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "Task rejected (queue full). Consider increasing capacity.", e);
        }
    }

    @Override
    public <T> Future<T> submitTask(Callable<T> task) {
        try {
            return executor.submit(task);
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "Task rejected (queue full). Consider increasing capacity.", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
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

    @Override
    public void logStatus() {
        Log.d(TAG, String.format("Pool: active=%d, queued=%d, completed=%d",
                executor.getActiveCount(),
                executor.getQueue().size(),
                executor.getCompletedTaskCount()));
    }
}
