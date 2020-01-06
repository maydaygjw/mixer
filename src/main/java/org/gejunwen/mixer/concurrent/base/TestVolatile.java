package org.gejunwen.mixer.concurrent.base;

import java.util.concurrent.atomic.AtomicReference;

public class TestVolatile implements Runnable {

    class Foo {
        boolean flag = true;
    }

//    private Foo foo = new Foo();

    private AtomicReference<Foo> ar = new AtomicReference<>(new Foo());

    public void stop(){
        ar.get().flag = false;
    }

    @Override
    public void run() {
        while (ar.get().flag){

        }
    }


    public static void main(String[] args) throws InterruptedException {
        TestVolatile test = new TestVolatile();
        Thread t = new Thread(test);
        t.start();

        Thread.sleep(1000);
        test.stop();
    }
}
