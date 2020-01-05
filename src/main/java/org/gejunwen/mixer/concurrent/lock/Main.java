package org.gejunwen.mixer.concurrent.lock;

import static org.gejunwen.mixer.utils.DebugUtils.p;

public class Main {

    public static void main(String[] args) {

        RedisLock lock = RedisLock.getInstance();

        for (int i = 0; i < 5; i++) {
            new CompetitionTask("Thread:" + i, lock).start();
        }

    }
}

class CompetitionTask extends Thread {

    private RedisLock redisLock;

    public CompetitionTask(String threadName, RedisLock redisLock) {
        super(threadName);
        this.redisLock = redisLock;
    }

    @Override
    public void run() {
        boolean locked = redisLock.lock("1", String.valueOf(System.currentTimeMillis() + 1000));
        if (locked) {
            p("Acquired lock!");
        } else {
            p("Failed to acquired lock");
        }
    }
}
