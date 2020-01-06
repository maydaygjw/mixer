package org.gejunwen.mixer.concurrent.base;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicReferenceExample {

    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final int THREAD_SIZE = 5;

    private int nInt = 0;
    private volatile int vInt = 0;
    private volatile Integer vInteger = 0;
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public AtomicInteger getAtomicInteger() {
        return atomicInteger;
    }

    public int getVInt() {
        return vInt;
    }

    private void nIntIncr() {
        nInt++;
    }

    private void vIntIncr() {
        vInt++;
    }

    private static void testVolatileView() {
        AtomicReferenceExample example = new AtomicReferenceExample();
        //设置nint监听线程
        for (int i = 0; i < THREAD_SIZE; i++) {
            POOL.execute(() -> {
                boolean flag = true;
                while (flag) {
                    if (example.nInt > 0) {
                        System.out.println("监听到nint值改变 time : " + System.currentTimeMillis());
                        flag = false;
                    }
                }
            });
            //设置vint监听线程
            POOL.execute(() -> {
                boolean flag = true;
                while (flag) {
                    if (example.vInt > 0) {
                        System.out.println("监听到vint值改变 time : " + System.currentTimeMillis());
                        flag = false;
                    }
                }
            });
        }

        System.out.println("提交更改");
        example.vIntIncr();
        example.nIntIncr();
        System.out.println("执行更改值完成 time :" + System.currentTimeMillis() +"  nint = " + example.nInt + "  vint = " + example.vInt);
        System.out.println("提交执行完毕");
    }

    private static void testVolatileAtomic() throws InterruptedException {
        int threadSize = 30;
        AtomicReferenceExample example = new AtomicReferenceExample();
        CountDownLatch countDownLatch = new CountDownLatch(threadSize * 2);
        for (int i = 0; i < threadSize; i++) {
            POOL.execute(() -> {
                for (int j = 0; j < 5000; j++) {
                    example.vIntIncr();
                }
                countDownLatch.countDown();
            });
            POOL.execute(() -> {
                for (int j = 0; j < 5000; j++) {
                    example.getAtomicInteger().incrementAndGet();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("最终结果 vint = " + example.getVInt());
        System.out.println("最终结果 atomicInt = " + example.getAtomicInteger().get());
        POOL.shutdown();
    }


    public static void main(String[] args) throws Exception {
        testVolatileAtomic();
    }

}
