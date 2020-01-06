package org.gejunwen.mixer.concurrent.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.gejunwen.mixer.utils.DebugUtils.p;


/**
 * 自己实现的BlockingQueue
 * 使用Lock和condition
 * @param <T>
 */
public class BlockedQueue<T> {

    final Lock lock = new ReentrantLock(true);

    private Object[] elements = new Object[5];
    private final int maxSize = 20;
    private int currentSize = 0;

    //条件变量：队列不空
    final Condition notFull = lock.newCondition();

    //条件变量：队列不满
    final Condition notEmpty = lock.newCondition();

    //入队
    BlockedQueue<T> enq(T t) {
        lock.lock();

        try {
            while(elements.length >= maxSize) {
                notEmpty.await();
            }
            //在队尾添加元素
            if(currentSize == elements.length) {
                //扩容
                Object[] newArray = new Object[elements.length + 5];
                System.arraycopy(elements, 0, newArray, 0, elements.length);
                elements = newArray;
            }

            elements[currentSize++] = t;
            if(currentSize == 1) {
                notFull.signalAll();
            }

            return this;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }


    }

    //出队
    T deq() {
        lock.lock();
        try {
            while(currentSize == 0) {
                notFull.await();
            }

            //从队头获取元素并将数组重新移位
            @SuppressWarnings("unchecked") T t = (T) elements[0];
            for(int i=0; i<currentSize - 1; i++) {
                elements[i] = elements[i+1];
            }
            elements[--currentSize] = null;

            if(currentSize == maxSize - 1) {
                notEmpty.signalAll();
            }

            return t;

        } catch(InterruptedException e) {
          throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BlockedQueue<String> queue = new BlockedQueue<>();
        queue.enq("1").enq("2").enq("3");

        p(queue.deq());
        p(queue.deq());

        queue.enq("4").enq("5").enq("6");
        p(queue.deq());
        p(queue.deq());
        p(queue.deq());
        p(queue.deq());
    }
}
