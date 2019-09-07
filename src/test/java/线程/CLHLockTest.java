package 线程;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: lanxinghua
 * Date: 2019/9/7 10:27
 * Desc: CLH
 */
public class CLHLockTest {
    private CLHLock lock = new CLHLock();

    public static void main(String[] args) {
        CLHLockTest test = new CLHLockTest();
        Runnable run = new Runnable() {
            private int a;
            @Override
            public void run() {
                test.lock.lock();
                for (int i = 0; i < 10000; i++) {
                    a++;
                }
                System.out.println(Thread.currentThread().getName() + " a = " + a);
                test.lock.unlock();
            }
        };
        for (int i = 0; i < 5; i++) {
            new Thread(run).start();
        }
    }

    class CLHLock{
        class QNode{
            private boolean locked = false;
        }

        private final AtomicReference<QNode> tail;
        private final ThreadLocal<QNode> mypred;
        private final ThreadLocal<QNode> myNode;

        public CLHLock(){
            tail = new AtomicReference<>(new QNode());
            mypred = new ThreadLocal<QNode>(){
                @Override
                protected QNode initialValue() {
                    return null;
                }
            };
            myNode = new ThreadLocal<QNode>(){
                @Override
                protected QNode initialValue() {
                    return new QNode();
                }
            };
        }

        public void lock(){
            QNode node = myNode.get();
            node.locked = true;
            QNode pred = tail.getAndSet(node);
            mypred.set(pred);
            // 如果前驱节点一直为true需要锁，就自旋
            while (pred.locked){
                //System.out.println(Thread.currentThread().getName() + "-自旋");
            }
        }

        public void unlock(){
            QNode node = myNode.get();
            node.locked = false;
            myNode.set(mypred.get());
        }
    }
}
