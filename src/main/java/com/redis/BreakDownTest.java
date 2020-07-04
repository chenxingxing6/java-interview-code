package com.redis;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * created by lanxinghua@2dfire.com on 2020/7/4
 * 缓存击穿方案
 * 1.synchronized粒度大
 * 2.synchronized粒度小
 * 3.synchronized + double check
 * 4.hystrix 支持将请求结果缓存起来
 *
 */
public class BreakDownTest {
    private static Map<String,String> cached = new HashMap<>();
    private static Object object = new Object();
    public static void main(String[] args) {
        long starTime = System.currentTimeMillis();
        CyclicBarrier barrier = new CyclicBarrier(100, new Runnable() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis() - starTime);
            }
        });
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            new Thread(()->{
                try {
                    test1();
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    // 方案1 1075s
    public static synchronized void test1(){
        String key = "key";
        if (cached.get(key) == null){
            String s = queryDb();
            cached.put(key, s);
        }
        System.out.println("缓存中查询数据：" + cached.get(key));
        return;
    }


    // 方案2 太慢了
    public static void test2(){
        String key = "key";
        if (cached.get(key) == null){
            synchronized (object){
                String s = queryDb();
                cached.put(key, s);
            }
        }
        System.out.println("缓存中查询数据：" + cached.get(key));
        return;
    }

    // 方案3 1050（支持）
    public static void test3(){
        String key = "key";
        if (cached.get(key) == null){
            synchronized (object){
                if (cached.get(key) != null){
                    System.out.println("缓存中查询数据：" + cached.get(key));
                    return;
                }
                String s = queryDb();
                cached.put(key, s);
            }
        }
        System.out.println("缓存中查询数据：" + cached.get(key));
        return;
    }


    // 方案4
//    public static void test4(){
//        HystrixRequestContext context = HystrixRequestContext.initializeContext();
//        try {
//            String key = "key";
//            new HystrixCommandCache(key).execute();
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            context.shutdown();
//        }
//        return;
//    }

    static class HystrixCommandCache extends HystrixCommand<String>{
        private final String key;

        public HystrixCommandCache(String key){
            super(HystrixCommandGroupKey.Factory.asKey("RequestCacheCommandGroup"));
            this.key = key;
        }

        @Override
        protected String run() throws Exception {
            String s = queryDb();
            return s;
        }

        @Override
        protected String getCacheKey() {
            return key;
        }
    }


    public static String queryDb(){
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("db查询数据........");
            return "*********************";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
