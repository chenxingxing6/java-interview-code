package com.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/9/2 15:25
 * Desc:
 */
@Component
public class Hello {
    // 无返回值
    @Async("taskExecutor")
    public void say(String msg){
        System.out.println("我是异步方法.....say " + msg);
    }

    // 有返回值
    @Async("taskExecutor")
    public ListenableFuture<String> sayWithReturn(String msg){
        try {
            TimeUnit.SECONDS.sleep(2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new AsyncResult<>("我是有返回值异步方法..." + msg);
    }


}
