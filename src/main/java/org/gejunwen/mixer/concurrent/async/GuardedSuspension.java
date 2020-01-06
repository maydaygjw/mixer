package org.gejunwen.mixer.concurrent.async;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static org.gejunwen.mixer.utils.DebugUtils.p;


//改成静态方法
public class GuardedSuspension {

    public static void main(String[] args) {

        WebRequestHandler handler = new WebRequestHandler();

        //模拟三秒后收到消息
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                p("");
            }
            handler.onMessage("ResultMessage");
        }).start();

        //异步转同步
        String result = handler.service("request");
        p("result is:" + result);
    }
}

class WebRequestHandler {

    private GuardedObject<String> go;

    public String service(String request) {
        //获取请求
        p("process request for: " + request);
        //调用消息队列
        sendToMessageQueue(request);

        //等待消息队列回调
        go = new GuardedObject<>();
        return go.get(Objects::nonNull);
    }

    private void sendToMessageQueue(String message) {
        p("sending message to message queue: " + message);
    }

    public void onMessage(String message) {
        go.fireEvent(message);
    }
}

class GuardedObject<T> {

    private T obj;

    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();

    T get(Predicate<T> predicate) {
        lock.lock();
        try {
            while(!predicate.test(obj)) {
                done.await(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return obj;
    }

    public void fireEvent(T t) {
        onChanged(t);
    }

    private void onChanged(T obj) {
        this.obj = obj;
        try {
            lock.lock();
            done.signalAll();
        }finally {
            lock.unlock();
        }

    }
}