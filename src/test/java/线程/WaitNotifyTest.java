package 线程;

import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/9/2 10:45
 * Desc: wait notify 运行的方法一定要加上synchronized
 */
public class WaitNotifyTest {
    public static void main(String[] args) throws Exception{
        Flag flag = new Flag();
        for (int i = 0; i < 10; i++) {
            new Runner("no"+i, flag).start();
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println("..................");
        flag.go();
    }
}

class Runner extends Thread {
    private String no;

    private Flag flag;

    public Runner(String no, Flag flag){
        this.no = no;
        this.flag = flag;
        System.out.println("奔跑者 +【"+ no +"】加入队列......");
    }

    @Override
    public void run() {
        flag.waitGo(no);
        System.out.println("奔跑者 +【"+ no +"】出发......");
    }
}

class Flag {
    public synchronized void waitGo(String no){
        try {
            System.out.println("奔跑者 +【"+ no +"】就绪......");
            wait();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void go(){
        notifyAll();
    }
}
