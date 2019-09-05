package 线程;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * User: lanxinghua
 * Date: 2019/9/5 20:28
 * Desc: AQS深入学习，我们反向推理
 * 假设有个减少库存的方法
 */
public class AQSTest {

    // 线程来抢占该资源
    private volatile int status = 0;

    // 独占锁线程
    private static Thread lockHolder;

    // 队列
    private ConcurrentLinkedQueue<Thread> queue = new ConcurrentLinkedQueue();

    private static Unsafe unsafe = UnsafeInstance.getInstance();

    // 偏移量
    private static long statusOffset = 0;

    static {
        try {
            statusOffset = unsafe.objectFieldOffset(AQSTest.class.getDeclaredField("status"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getStatus() {
        return status;
    }

    public static void main(String[] args) {
        AQSTest test = new AQSTest();
        for (int i = 0; i < 10; i++) {
            final int ii = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test.desStockLock("用户-" + ii);
                }
            }).start();
            System.out.println();
        }
    }


    /**
     * 原子修改，一次性执行完
     * @param expert 期待值
     * @param update 更新值
     * @return
     */
    private boolean compareAndSwapStatus(int expert, int update){
        return unsafe.compareAndSwapInt(this, statusOffset, expert, update);
    }


    /**
     * 减少库存
     */
    public boolean desStockLock(String userName){
        // 此处加锁 T1,T2,T3
        // 1.让所有线程都进入循环
        Thread thread = Thread.currentThread();
        for (;;){
            int status = getStatus();
            if (status == 0){
                // TODO: tip status++,这样在高并发下不是原子性的（1、status值拷贝到工作内存， 2、进行自加操作）
                // 2.利用cas算法，比较并交换，看谁抢占资源
                if (compareAndSwapStatus(0, 1)){
                    // 加锁成功
                    lockHolder = thread;
                    System.out.println("加锁成功 " + lockHolder.getName());
                    break;
                }
            }
            // 3.阻塞线程进入队列FIFO
            System.out.println("阻塞线程进入队列....." + thread.getName());
            queue.add(thread);
            // T2，T3 加锁失败
            // TODO: tip 如果不加阻塞，会大量消耗机器性能，T1减库存耗时特别长，导致许多线程在循环，导致机器挂了
            LockSupport.park();
        }

        // 获取库存
        StockService stockService = new StockService();
        Integer stock = stockService.getStock();
        if (stock <= 0){
            System.out.println("库存为0,下单失败");
        }
        stock--;
        stockService.update(stock);
        System.out.println(userName + "下单成功");

        // 此处释放锁
        for (;;){
            int status = getStatus();
            if (status !=0 && lockHolder == thread){
                compareAndSwapStatus(status, 0);
                System.out.println("释放锁成功 " + lockHolder.getName());
                break;
            }
            // 通知唤醒被阻塞的线程
            Thread t = queue.poll();
            LockSupport.unpark(t);
        }
        return true;
    }



    static class UnsafeInstance{
        // 反射获取Unsafe类
        public static Unsafe getInstance(){
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                return (Unsafe) field.get(null);
            }catch (Exception e){
                e.printStackTrace();
            }
           return null;
        }
    }
}

// 库存服务
class StockService{

    public StockService(){

    }

    int getStock(){
        return 10;
    }

    boolean update(int stock){
        return true;
    }
}


