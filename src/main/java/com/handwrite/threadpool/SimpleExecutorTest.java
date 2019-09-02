package com.handwrite.threadpool;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * User: lanxinghua
 * Date: 2019/9/2 20:46
 * Desc:
 */
public class SimpleExecutorTest {

    public static void main(String[] args) throws InterruptedException {

        SimpleThreadPoolExecutor executor = new SimpleThreadPoolExecutor();
        IntStream.range(0, 30).forEach(i ->
                executor.submit(() -> {
                    System.out.printf("[线程] - [%s] 开始工作...\n", Thread.currentThread().getName());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("[线程] - [%s] 工作完毕...\n", Thread.currentThread().getName());
                })
        );
        //executor.shutdown();如果放开注释即会执行完所有任务关闭线程池
    }
}
