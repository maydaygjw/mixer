package org.gejunwen.mixer.concurrent.lock;

import org.apache.commons.lang3.StringUtils;

public class RedisLock {

    private MockRedis mockRedis = new MockRedis();

    private RedisLock() {}

    private static RedisLock singleton;

    public static RedisLock getInstance() {
        if(singleton == null) {
            synchronized (RedisLock.class) {
                if(singleton == null) {
                    singleton = new RedisLock();
                }
            }
        }
        return singleton;
    }

    public boolean lock(String key, String value) {
        //situation1: 缓存内不存在，加锁成功
        if(mockRedis.setIfAbsent(key,  value)) {
            return true;
        }

        String currentValue = mockRedis.get(key);

        //situation2: 时间戳过期，加锁成功
        if(!StringUtils.isEmpty(currentValue) && System.currentTimeMillis()  > Long.parseLong(currentValue)) {

            //situation3: 多个线程并发进入，只有一个加锁成功
            String oldValue = mockRedis.getAndSet(key, value);
            return !StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue);
        }

        return false;
    }
}
