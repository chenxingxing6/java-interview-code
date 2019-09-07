package 线程;

import com.handwrite.threadpool.SimpleThreadPoolExecutor;
import com.handwrite.threadpool.my.IMyExecutorService;
import com.handwrite.threadpool.my.MyThreadPoolExecutor;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/9/2 18:44
 * Desc:
 */
public class MyThreadPoolTest {
    // SimpleThreadPoolExecutor executor = new SimpleThreadPoolExecutor();
    private static ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    IMyExecutorService myExecutor = new MyThreadPoolExecutor(
            2,
            100,
            3,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue(1000),
            new MyThreadPoolExecutor.MyAbortPolicy());


    @Test
    public void test00(){
        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 1; i <= 1000; i++) {
            final int ii = i;
            myExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("test00-执行任务------线程" + ii + "："+ Thread.currentThread());
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        watch.stop();
        System.out.println("**********************" + watch.getTotalTimeSeconds() +"s");
    }


    @Test
    public void test01(){
        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 1; i <= 1000; i++) {
            final int ii = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("test2-执行任务------线程" + ii + "："+ Thread.currentThread());
                }
            }).start();
        }
        watch.stop();
        System.out.println("**********************" + watch.getTotalTimeSeconds() +"s");
    }

    @Test
    public void test02(){
        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 1; i <= 1000; i++) {
            final int ii = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        }
        watch.stop();
        System.out.println("**********************" + watch.getTotalTimeSeconds() +"s");
    }

    @Test
    public void test03(){
        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 1; i <= 1000; i++) {
            final int ii = i;
            executor1.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    System.out.println("test2-执行任务------线程" + ii + "："+ Thread.currentThread());
                }
            });
        }
        watch.stop();
        System.out.println("**********************" + watch.getTotalTimeSeconds() +"s");
    }
}
