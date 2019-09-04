package 线程;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/9/4 10:11
 * Desc: 循环栅栏，使用场景：多线程计算数据，最后合并计算结果
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(2, new Runnable() {
            @Override
            public void run() {
                System.out.println("都搞定....");
            }
        });
        for (int i = 0; i < 2; i++) {
            new TaskThread(barrier).start();
        }
    }

    static class TaskThread extends Thread {
        private CyclicBarrier barrier;

        public TaskThread(CyclicBarrier barrier){
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                System.out.println(getName() + " 到达栅栏 A");
                barrier.await();
                System.out.println(getName() + " 冲破栅栏 A");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
