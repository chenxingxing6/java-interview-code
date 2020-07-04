package com.hystrix;

import com.netflix.hystrix.*;

import java.util.concurrent.TimeUnit;

/**
 * created by lanxinghua@2dfire.com on 2020/7/5
 */
public class CommandBase extends HystrixCommand<String> {
    private static final int MAX_POOL_SIZE = 200;
    private static final int KEEP_ALIVE_TIME_MINUTES = 5;
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private String name;
    private String groupName;
    private String poolName;

    public CommandBase(String name, String groupName, String poolName){
        super(
                // 服务分组
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupName))
                // 线程分组
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(poolName))
                // 线程池配置
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                        .withCoreSize(CORE_POOL_SIZE)
                        .withKeepAliveTimeMinutes(KEEP_ALIVE_TIME_MINUTES)
                        .withMaximumSize(MAX_POOL_SIZE)
                        .withQueueSizeRejectionThreshold(10000)
                )
                // 线程池隔离
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                )
        );
        this.name = name;
        this.groupName = groupName;
        this.poolName = poolName;
    }

    @Override
    protected String run() throws Exception {
        TimeUnit.MILLISECONDS.sleep(100);
        return "name = " + name + ",groupName = " + groupName + ",poolName = " + poolName + System.currentTimeMillis();
    }

    public static void main(String[] args) {
        CommandBase phone = new CommandBase("手机", "phoneGroup", "phonePool");
        CommandBase tv = new CommandBase("电视", "tvGroup", "tvPool");

        System.out.println(phone.execute());
        System.out.println(tv.execute());
    }
}
