package 线程;

import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/9/4 13:54
 * Desc:
 */
public class ThreadLocalTest {
    final static ThreadLocal local1 = new ThreadLocal();
    final static ThreadLocal local2 = new ThreadLocal();


    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                local1.set("A");
                local2.set(1);
                try {
                    TimeUnit.SECONDS.sleep(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
                System.out.println(local1.get());
                System.out.println(local2.get());
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                System.out.println(local1.get());
                System.out.println(local2.get());
            }
        }).start();
    }
}
