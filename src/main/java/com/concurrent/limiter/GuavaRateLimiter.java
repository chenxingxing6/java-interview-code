package com.concurrent.limiter;

import com.google.common.util.concurrent.RateLimiter;

/**
 * created by lanxinghua@2dfire.com on 2020/6/26
 * 限流Guava工具包：令牌桶算法
 */
public class GuavaRateLimiter {
    public static void main(String[] args) {
        // 1.创建限流器，每秒5个令牌(令牌桶算法)
        RateLimiter limiter = RateLimiter.create(5);
        while (true){
            new Thread(()->{
                limiter.acquire();
                System.out.println("*****"+Thread.currentThread().getName());
            }).start();
        }
    }
}
