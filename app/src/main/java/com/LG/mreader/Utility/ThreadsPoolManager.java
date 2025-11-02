package com.LG.mreader.Utility;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface ThreadsPoolManager {
    public void submitTask(Runnable task);
    public <T> Future<T> submitTask(Callable<T> task);
    public void shutdown();
    public void logStatus();
}
