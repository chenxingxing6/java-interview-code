package com.handwrite.threadpool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: lanxinghua
 * Date: 2019/9/2 20:38
 * Desc:
 */
public class SimpleThreadPoolExecutor extends Thread {

    // 任务线程状态
    private enum TaskState {
        FREE, RUNNABLE, BLOCKED, TERMINATED;
    }

    //拒绝策略接口
    interface DiscardPolicy {
        void discard() throws DiscardException;
    }
    static class DiscardException extends RuntimeException {
        DiscardException(String message) {
            super(message);
        }
    }

    private final static DiscardPolicy DEFAULT_DISCARD_POLICY = () -> {
        throw new DiscardException("[拒绝执行] - [任务队列溢出...]");
    };



    // 线程池大小
    private int threadPoolSize;
    // 最大接收任务
    private int queueSize;
    // 拒绝策略
    private DiscardPolicy discardPolicy;
    // 线程池是否被销毁
    private volatile boolean destroy = false;

    // 默认最小线程数
    private final static int DEFAULT_MIN_THREAD_SIZE = 2;
    // 活跃线程
    private final static int DEFAULT_ACTIVE_THREAD_SIZE = 5;
    // 最大线程
    private final static int DEFAULT_MAX_THREAD_SIZE = 10;
    // 最多执行多少任务
    private final static int DEFAULT_WORKER_QUEUE_SIZE = 100;
    // 线程名前缀
    private final static String THREAD_NAME_PREFIX = "线程名称-";
    // 线程组的名称
    private final static String THREAD_POOL_NAME = "线程组-";
    // 线程组
    private final static ThreadGroup THREAD_GROUP = new ThreadGroup(THREAD_POOL_NAME);
    // 线程容器
    private final static List<WorkerTask> WORKER_TASKS = new ArrayList<>();
    // 任务队列容器,也可以用Queue<Runnable> 遵循 FIFO 规则
    private final static LinkedList<Runnable> TASK_QUEUE = new LinkedList<>();


    private int minSize;      //最小线程
    private int maxSize;      //最大线程
    private int activeSize;   //活跃线程

    public SimpleThreadPoolExecutor() {
        this(DEFAULT_MIN_THREAD_SIZE, DEFAULT_ACTIVE_THREAD_SIZE, DEFAULT_MAX_THREAD_SIZE, DEFAULT_WORKER_QUEUE_SIZE, DEFAULT_DISCARD_POLICY);
    }

    SimpleThreadPoolExecutor(int minSize, int activeSize, int maxSize, int queueSize, DiscardPolicy discardPolicy){
        this.minSize = minSize;
        this.activeSize = activeSize;
        this.maxSize = maxSize;
        this.queueSize = queueSize;
        this.discardPolicy = discardPolicy;
        initPool();
    }

    public void submit(Runnable runnable) {
        if (destroy) {
            throw new IllegalStateException("线程池已销毁...");
        }
        synchronized (TASK_QUEUE) {
            if (TASK_QUEUE.size() > queueSize) { //如果当前任务队超出队列限制,后续任务拒绝执行
                discardPolicy.discard();
            }
            // 1.将任务添加到队列
            TASK_QUEUE.addLast(runnable);
            // 2.唤醒等待的线程去执行任务
            TASK_QUEUE.notifyAll();
        }
    }

    void shutdown() throws InterruptedException {
        int activeCount = THREAD_GROUP.activeCount();
        while (!TASK_QUEUE.isEmpty() && activeCount > 0) {
            // 如果还有任务,那就休息一会
            Thread.sleep(100);
        }
        int intVal = WORKER_TASKS.size();//如果线程池中没有线程,那就不用关了
        while (intVal > 0) {
            for (WorkerTask task : WORKER_TASKS) {
                //当任务队列为空的时候，线程状态才会为 BLOCKED ，所以可以打断掉，相反等任务执行完在关闭
                if (task.taskState == TaskState.BLOCKED) {
                    task.close();
                    intVal--;
                } else {
                    Thread.sleep(50);
                }
            }
        }
        this.destroy = true;
        //资源回收
        TASK_QUEUE.clear();
        WORKER_TASKS.clear();
        this.interrupt();
        System.out.println("线程关闭");
    }

    private void createWorkerTask() {
        WorkerTask task = new WorkerTask();
        // 刚创建出来的线程应该是未使用的
        task.taskState = TaskState.FREE;
        WORKER_TASKS.add(task);
        task.start();
    }

    /**
     * 初始化操作
     */
    private void initPool() {
        for (int i = 0; i < this.minSize; i++) {
            this.createWorkerTask();
        }
        this.threadPoolSize = minSize;
        // 自己启动自己
        this.start();
    }

    @Override
    public void run() {
        while (!destroy) {
            try {
                Thread.sleep(5_000L);
                if (TASK_QUEUE.size() > activeSize && threadPoolSize < activeSize) { // 第一次扩容到 activeSize 大小
                    for (int i = threadPoolSize; i < activeSize; i++) {
                        createWorkerTask();
                    }
                    this.threadPoolSize = activeSize;
                    System.out.println("[初次扩充] - [" + toString() + "]");
                } else if (TASK_QUEUE.size() > maxSize && threadPoolSize < maxSize) {// 第二次扩容到最大线程
                    System.out.println();
                    for (int i = threadPoolSize; i < maxSize; i++) {
                        createWorkerTask();
                    }
                    this.threadPoolSize = maxSize;
                    System.out.println("[再次扩充] - [" + toString() + "]");
                } else {
                    //防止线程在submit的时候，其他线程获取到锁干坏事
                    synchronized (WORKER_TASKS) {
                        int releaseSize = threadPoolSize - activeSize;
                        Iterator<WorkerTask> iterator = WORKER_TASKS.iterator();// List不允许在for中删除集合元素,所以这里需要使用迭代器
                        while (iterator.hasNext()) {
                            if (releaseSize <= 0) {
                                break;
                            }
                            WorkerTask task = iterator.next();
                            //不能回收正在运行的线程,只回收空闲线程
                            if (task.taskState == TaskState.FREE) {
                                task.close();
                                iterator.remove();
                                releaseSize--;
                            }
                        }
                        System.out.println("[资源回收] - [" + toString() + "]");
                    }
                    threadPoolSize = activeSize;
                }
            } catch (InterruptedException e) {
                System.out.println("资源释放");
            }
        }
    }

    @Override
    public String toString() {
        return "SimpleThreadPoolExecutor{" +
                "threadPoolSize=" + threadPoolSize +
                ", taskQueueSize=" + TASK_QUEUE.size() +
                ", minSize=" + minSize +
                ", maxSize=" + maxSize +
                ", activeSize=" + activeSize +
                '}';
    }



    public static class WorkerTask extends Thread {
        // 线程状态
        private TaskState taskState;
        // 线程编号
        private static int threadInitNumber;
        // 生成线程名,参考Thread.nextThreadNum();
        private static synchronized String nextThreadName() {
            return THREAD_NAME_PREFIX + (++threadInitNumber);
        }

        WorkerTask() {
            super(THREAD_GROUP, nextThreadName());
        }

        @Override
        public void run() {
            Runnable target;
            //说明该线程处于空闲状态
            OUTER:
            while (this.taskState != TaskState.TERMINATED) {
                synchronized (TASK_QUEUE) {
                    while (this.taskState == TaskState.FREE && TASK_QUEUE.isEmpty()) {
                        try {
                            this.taskState = TaskState.BLOCKED;//此处标记
                            //没有任务就wait住,让出CPU执行权
                            TASK_QUEUE.wait();
                            //如果被打断说明当前线程执行了 shutdown() 方法  线程状态为 TERMINATED 直接跳到 while 便于退出
                        } catch (InterruptedException e) {
                            break OUTER;
                        }
                    }
                    target = TASK_QUEUE.removeFirst();//遵循FIFO策略
                }
                if (target != null) {
                    this.taskState = TaskState.RUNNABLE;
                    target.run();//开始任务了
                    this.taskState = TaskState.FREE;
                }
            }
        }

        void close() {//优雅关闭线程
            this.taskState = TaskState.TERMINATED;
            this.interrupt();
        }
    }
}
