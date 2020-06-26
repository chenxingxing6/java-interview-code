package com.concurrent.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * created by lanxinghua@2dfire.com on 2020/6/26
 * 项目中使用场景：
 * 1.轮询支付宝小程序构建状态
 * 2.发送短信
 */
public class RetryerTest {

    public static void main(String[] args) throws Exception {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .retryIfRuntimeException()
                .retryIfResult(Predicates.equalTo(false))
                // 尝试请求6次
                .withStopStrategy(StopStrategies.stopAfterAttempt(6))
                // 等待策略
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                // 时间限制
                .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(2, TimeUnit.SECONDS))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("----------------start---------------");
                        System.out.println("重试次数："+attempt.getAttemptNumber());
                        if (attempt.hasException()){
                            System.out.println("重试发生异常：" + attempt.getExceptionCause());
                        }
                        if (attempt.hasResult()){
                            System.out.println("结果：" + attempt.getResult());
                        }
                        System.out.println("-----------------end--------------");
                    }
                })
                .build();

        Boolean call = retryer.call(new Callable<Boolean>() {
            int times = 1;

            @Override
            public Boolean call() throws Exception {
                times++;
                String auditStatus = getAuditStatus();
                boolean result = "SUCESS".equals(auditStatus);
                if (times == 2) {
                    System.out.println("NullPointerException...");
                    throw new NullPointerException();
                } else if (times == 3) {
                    System.out.println("Exception...");
                    throw new Exception();
                } else if (times == 4) {
                    System.out.println("RuntimeException...");
                    throw new RuntimeException();
                } else if (times == 5) {
                    System.out.println("false...");
                    return result;
                } else {
                    System.out.println("true...");
                    return result;
                }
            }
        });
        System.out.println("结果："+call);
    }

    private static String getAuditStatus(){
        return "SUCESS";
    }
}
