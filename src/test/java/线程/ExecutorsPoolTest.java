package 线程;

import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: lanxinghua
 * Date: 2019/9/2 11:36
 * Desc: 线程池
 */
public class ExecutorsPoolTest {
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }


    public static void main(String[] args) {
        ExecutorsPoolTest test = new ExecutorsPoolTest();
        System.out.println(isRunning(test.ctl.get()));
    }

    @Test
    public void test01(){
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            final int ii = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + "\ttask"+ ii +"正在执行.....");
                        System.out.println(Thread.currentThread().getName() + "\ttask"+ ii +"执行完毕.....");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
    }

    @Test
    public void test02(){
        int i = 0;
        for (;;){
            i ++;
            System.out.println(i);
            if (i > Integer.MAX_VALUE){
                break;
            }
        }
    }
}
