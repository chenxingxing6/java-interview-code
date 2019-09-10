package 线程;

/**
 * User: lanxinghua
 * Date: 2019/9/10 14:57
 * Desc:
 */
public class SynchronizedTest {

    public static void main(String[] args) {
        synchronized (SynchronizedTest.class){
            System.out.println("666");
        }
    }
}
