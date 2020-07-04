package com.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * created by lanxinghua@2dfire.com on 2020/7/5
 * Request Cache
 */
public class HystrixRequestCache extends HystrixCommand<Boolean> {
    private final int value;
    public HystrixRequestCache(int value){
        super(HystrixCommandGroupKey.Factory.asKey("group"));
        this.value = value;
    }

    public static void main(String[] args) {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            Boolean execute1 = new HystrixRequestCache(100).execute();
            System.out.println(execute1);

            Boolean execute2 = new HystrixRequestCache(1).execute();
            System.out.println(execute2);

            System.out.println("----------测试缓存----------");
            HystrixRequestCache cache1 = new HystrixRequestCache(2);
            cache1.execute();
            System.out.println(cache1.isResponseFromCache);

            HystrixRequestCache cache2 = new HystrixRequestCache(2);
            cache2.execute();
            System.out.println(cache2.isResponseFromCache);
        }finally {
            context.shutdown();
        }
    }


    @Override
    protected Boolean run() throws Exception {
        System.out.println("run.....");
        return value == 0 || value %2 == 0;
    }

    @Override
    protected String getCacheKey() {
        return String.valueOf(value);
    }
}
