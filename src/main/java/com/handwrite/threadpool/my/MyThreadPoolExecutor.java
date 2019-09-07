package com.handwrite.threadpool.my;

import com.handwrite.threadpool.my.reject.IRejectExecHandler;
import com.handwrite.threadpool.my.reject.MyRejectException;

import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: lanxinghua
 * Date: 2019/9/7 14:42
 * Desc: 模仿ThreadPoolExecutor实现自己的线程池
 */
public class MyThreadPoolExecutor implements IMyExecutorService {

    public static void main(String[] args) {
        IMyExecutorService executor = new MyThreadPoolExecutor(
                2,
                10,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(100),
                new MyDiscardPolicy());
        for (int i = 0; i < 10; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("********");
                }
            });
        }

    }

    // 记录当前存活的线程数
    private AtomicInteger ctl = new AtomicInteger(0);
    private final HashSet<Worker> workers = new HashSet<Worker>();
    private final ReentrantLock mainLock = new ReentrantLock();
    // 核心线程大小
    private final int corePoolSize;
    // 最大线程池大小
    private final int maximumPoolSize;
    // 线程存活时间(纳秒）
    private final long keepAliveTime;
    private volatile boolean allowCoreThreadTimeOut;
    // 线程阻塞队列
    private final BlockingQueue<Runnable> workQueue;
    // 拒绝策略
    private final IRejectExecHandler handler;

    // 线程池状态
    private volatile boolean isShutDown = false;
    private long completedTaskCount;


    public MyThreadPoolExecutor(int corePoolSize,
                                int maximumPoolSize,
                                long keepAliveTime,
                                TimeUnit unit,
                                BlockingQueue<Runnable> workQueue,
                                IRejectExecHandler handler) {
        if (corePoolSize <= 0){
            throw new IllegalArgumentException("核心线程数不能为空");
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.workQueue = workQueue;
        this.handler = handler;
        if (keepAliveTime > 0){
            allowCoreThreadTimeOut = true;
        }
    }


    @Override
    public void execute(Runnable task) {
        if (task == null){
            throw new NullPointerException("任务不能为空");
        }
        if (isShutDown){
            throw new IllegalStateException("线程池已经关闭");
        }
        // 核心线程池还可以接收任务
        int c = ctl.get();
        if (c < corePoolSize){
            if(addWorker(task, true));
        }
        // 进入阻塞队列
        else if (workQueue.offer(task)){
            int recheck = ctl.get();
            if (recheck < maximumPoolSize) {
                addWorker(null, false);
            }
        }
        // 拒绝策略
        else {
            handler.reject(task, this);
        }
    }

    @Override
    public void shutdown() {
        this.isShutDown = true;
    }

    @Override
    public int getActiveThread() {
        return ctl.get();
    }

    @Override
    public Runnable getTask() {
        boolean timedOut = false;
        for (;;){
            int wc = ctl.get();
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
            if ((wc > maximumPoolSize || (timed && timedOut)) && (wc >1 || workQueue.isEmpty())){
                continue;
            }
            try {
                Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
                if (r != null){
                    return r;
                }
                timedOut = true;
            }catch (Exception e){
                timedOut = false;
            }
            break;
        }
        return null;
    }

    /**
     * 添加工作
     * @param task
     * @param core
     * @return
     */
    private boolean addWorker(Runnable task, boolean core){
        if (core){
            ctl.incrementAndGet();
        }
        boolean workerAdded = false;
        boolean workerStarted = false;
        Worker w = null;
        try {
            w = new Worker(task);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    if (!isShutDown){
                        if (t.isAlive()){ // 检查线程是否已经处于运行状态，start方法不能重复执行
                            throw new IllegalThreadStateException();
                        }
                        workers.add(w);
                        workerAdded = true;
                    }
                }finally {
                    mainLock.unlock();
                }
                if (workerAdded){
                    t.start();
                    workerStarted = true;
                }
            }
        }finally {
           if (!workerStarted){
               final ReentrantLock mainLock = this.mainLock;
               mainLock.lock();
               try {
                   if (w != null){
                       workers.remove(w);
                       ctl.decrementAndGet();
                   }
               } finally {
                   mainLock.unlock();
               }
           }
        }
        return workerStarted;
    }


    /**
     * 创建工作
     */
    class Worker extends ReentrantLock implements Runnable {
        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;


        public Worker(Runnable r) {
            this.firstTask = r;
            this.thread = new Thread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }
    }

    /**
     * 运行工作
     * @param w
     */
    private void runWorker(Worker w){
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null){
                w.lock();
                if (isShutDown && !wt.isInterrupted()){
                    wt.interrupt();
                }
                try {
                    task.run();
                }finally {
                    task = null;
                    // 线程完成数量
                    w.completedTasks ++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        }finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly){
            ctl.decrementAndGet();
        }
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        }finally {
            mainLock.unlock();
        }
        if (completedAbruptly && !workQueue.isEmpty()){
            addWorker(null, false);
        }
    }



    // 策略
    /**
     * 1.拒绝所有任务，并抛异常
     */
    public static class MyAbortPolicy implements IRejectExecHandler {
        public MyAbortPolicy() { }

        @Override
        public void reject(Runnable r, MyThreadPoolExecutor e) {
            throw new MyRejectException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }


    /**
     * 不处理，直接丢弃
     */
    public static class MyDiscardPolicy implements IRejectExecHandler {
        public MyDiscardPolicy() { }

        @Override
        public void reject(Runnable r, MyThreadPoolExecutor e) {

        }
    }
}
