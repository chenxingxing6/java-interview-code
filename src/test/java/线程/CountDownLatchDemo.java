package 线程;

import org.apache.commons.lang.time.StopWatch;

import java.util.concurrent.*;

/**
 * User: lanxinghua
 * Date: 2019/9/3 20:44
 * Desc: 发射火箭demo
 */
public class CountDownLatchDemo{
    private static final CountDownLatch latch = new CountDownLatch(10);

    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 0; i < 9; i++) {
            final int ii = i;
            executor.execute(() -> {
                // 模拟检查任务
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("check complete " + ii);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
        }

        System.out.println("-------开始await-------");

        // 等等检查
        latch.await(2, TimeUnit.SECONDS);

        // 发射火箭
        System.out.println("-------发射火箭-------");

        watch.stop();
        System.out.println(watch.getTime());
        executor.shutdown();

    }
}
