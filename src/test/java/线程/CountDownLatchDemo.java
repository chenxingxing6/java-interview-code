package 线程;

import java.util.concurrent.*;

/**
 * User: lanxinghua
 * Date: 2019/9/3 20:44
 * Desc: 发射火箭demo
 */
public class CountDownLatchDemo{
    private static final CountDownLatch latch = new CountDownLatch(10);

    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 模拟检查任务
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("check complete");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                latch.countDown();
            }
        }
    };


    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for (int i = 0; i < 9; i++) {
            executor.execute(runnable);
        }

        // 等等检查
        latch.await();

        // 发射火箭
        System.out.println("-------发射火箭-------");

        executor.shutdown();

    }
}
