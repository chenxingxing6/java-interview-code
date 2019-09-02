package 集合;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * User: lanxinghua
 * Date: 2019/8/31 10:24
 * Desc:
 */
public class CollectionTest {

    // set for循环输出不会按顺序输出
    @Test
    public void test01(){
        Set<String> sets = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            sets.add(i + "");
        }
        for (String set : sets) {
            System.out.println(set);
        }
    }

    // vector arraylist
    @Test
    public void test02(){
        List vector = new Vector(10, 20);
        HashMap map = new HashMap();
        Hashtable hashtable = new Hashtable();
        hashtable.put(null, 11);
        Enumeration enumeration = hashtable.elements();
    }

    // hashMap测试
    @Test
    public void test03(){
        Map<String, Object> map = new Hashtable<>();
        new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                //System.out.println(Thread.currentThread().getName() + "put "+ i);
                map.put(i + "", i);
            }
        }).start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "get" + map.get("20"));
        }).start();

    }


    final static HashMap<String, String> map = new HashMap<>();

    /**
     * 验证hashMap是线程不安全的
     * 读写不同步，操作不是原子性，内存的可见性
     */
    @Test
    public void test04(){
        new Thread(() -> {
            int j = 0;
            while(j < 10) {
                map.put("end", String.valueOf(j));
                System.out.println("put map:" + map);
                j ++;
            }
        }).start();
        //  启动线程
        for(int i = 0; i < 10; i++) {
            new Thread(new R(String.valueOf(i))).start();
        }
    }

    //  运行的线程
    public static class R implements Runnable {
        private String end;
        public R(String end) {
            super();
            this.end = end;
        }
        @Override
        public void run() {
            System.out.println("map" + map);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(!this.end.equals(map.get("end"))) {
                System.out.println(this.end);
            }
            System.out.println(this.end + ": is End!");
        }

    }
}
