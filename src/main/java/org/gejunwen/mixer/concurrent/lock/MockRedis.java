package org.gejunwen.mixer.concurrent.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟单机版本Redis
 */

public class MockRedis {

    private Map<String, String> mockCache = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    //Mock for redis SETNX
    public boolean setIfAbsent(String key, String value) {
        if(!mockCache.containsKey(key)) {
            synchronized (lock) {
                if(!mockCache.containsKey(key)) {
                    mockCache.put(key, value);
                    return true;
                }
            }
        }
        return false;
    }

    public String get(String key) {
        return mockCache.get(key);
    }

    //set the value for the key and return the old one if exists
    //it's an atomic operation
    //Mock for redis:GETSET
    public String getAndSet(String key, String value) {
        synchronized (this.lock) {
            String oldVal = mockCache.get(key);
            mockCache.put(key, value);
            return oldVal;
        }
    }
}
