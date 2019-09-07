package com.handwrite.threadpool.my;

/**
 * User: lanxinghua
 * Date: 2019/9/7 14:39
 * Desc:
 */
public interface IMyExecutorService {
    public void execute(Runnable runnable);

    public void shutdown();

    public int getActiveThread();

    public Runnable getTask();
}
