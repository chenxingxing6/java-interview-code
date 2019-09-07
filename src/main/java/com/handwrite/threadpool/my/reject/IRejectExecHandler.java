package com.handwrite.threadpool.my.reject;

import com.handwrite.threadpool.my.MyThreadPoolExecutor;

/**
 * User: lanxinghua
 * Date: 2019/9/7 14:49
 * Desc: 拒绝策略接口
 */
public interface IRejectExecHandler {

    public void reject(Runnable runnable, MyThreadPoolExecutor executor);
}
