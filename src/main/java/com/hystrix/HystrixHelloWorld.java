package com.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * created by lanxinghua@2dfire.com on 2020/7/5
 */
public class HystrixHelloWorld extends HystrixCommand<String> {
    private final String name;
    protected HystrixHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("group"));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        return "hello world " + name;
    }

    public static void main(String[] args) throws Exception{
        // 同步
        String lxh = new HystrixHelloWorld("lxh").execute();
        System.out.println(lxh);

        // 异步
        Future<String> lxh1 = new HystrixHelloWorld("lxh1").queue();
        System.out.println(lxh1.get(1, TimeUnit.SECONDS));

        // 响应式执行
        Observable<String> ho = new HystrixHelloWorld("lxh2").observe();
        // blocking
        String single = ho.toBlocking().single();
        System.out.println(single);

        // no blocking
        Subscription subscribe = ho.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("call");
            }
        });
    }
}
