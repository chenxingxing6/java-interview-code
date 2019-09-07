package 线程;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/9/7 13:38
 * Desc:
 */
public class ExchangerTest extends Thread{
    private Exchanger exchanger;
    private String data;
    private String threadName;

    public ExchangerTest(Exchanger exchanger, String data, String threadName) {
        this.exchanger = exchanger;
        this.data = data;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        try {
            System.out.println(threadName + ": " + exchanger.exchange(data, 1, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Exchanger exchanger = new Exchanger();
        ExchangerTest test1 = new ExchangerTest(exchanger, "data1", "thread-1");
        ExchangerTest test2 = new ExchangerTest(exchanger, "data2", "thread-2");
        ExchangerTest test3 = new ExchangerTest(exchanger, "data3", "thread-3");
        test3.start();
        test1.start();
        test2.start();
        // 剩下的未得到配对的线程，则会被阻塞，永久等待，直到与之配对的线程到达位置
    }
}
