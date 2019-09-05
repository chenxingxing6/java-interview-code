package 线程;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/9/5 09:42
 * Desc: 信号量测试
 */
public class SemaphoreTest {
    private static final Semaphore semaphore = new Semaphore(3);
    private static final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    static class ParkThread extends Thread{
        private String carName;

        public ParkThread(String carName){
            this.carName = carName;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println(carName + "-【进入】停车场");
                TimeUnit.SECONDS.sleep(2);
                semaphore.release();
                System.out.println(carName + "-【出】停车场");
            }catch (Exception e){

            }

        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new ParkThread("车" + i));
        }
    }

}
