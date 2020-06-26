package com.concurrent.limiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * created by lanxinghua@2dfire.com on 2020/6/26
 * 信号量控制并发数
 */
public class SemaphoreTest {
    private static Semaphore semaphore = new Semaphore(5);
    private static ExecutorService executorService = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {

        while (true){
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    System.out.println("*****"+Thread.currentThread().getName());
                    TimeUnit.MICROSECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            });
        }
    }
}
