package 线程;

/**
 * User: lanxinghua
 * Date: 2019/9/10 16:56
 * Desc: 指令重排序
 */
public class CmdReSortTest {
    int a = 0;
    static boolean flag = false;

    void write(){
        a = a+1;
        flag = true;
        System.out.println("a = " + a);
    }

    void read(){
        if (flag){
            int i = a + 1;
        }
    }
}
