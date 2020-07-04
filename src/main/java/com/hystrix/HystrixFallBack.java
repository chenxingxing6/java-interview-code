package com.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * created by lanxinghua@2dfire.com on 2020/7/5
 * 服务降级
 */
public class HystrixFallBack extends HystrixCommand<String> {
    private final String name;

    public HystrixFallBack(String name){
        super(HystrixCommandGroupKey.Factory.asKey("group"));
        this.name = name;
    }

    public static void main(String[] args) {
        String result = new HystrixFallBack("test").execute();
        System.out.println("运行结果：" + result);
    }

    @Override
    protected String run() throws Exception {
        System.out.println("do something...");
        throw new RuntimeException("异常..");
    }

    // 实现优雅降级
    @Override
    protected String getFallback() {
        return "hello world fallback " + name;
    }
}
