package com.zhangyj.java.base;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    private final int maxCacheSize;

    LRUCache(int initialCapacity, int maxCacheSize) {
        super(initialCapacity, 0.75F, true);
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return this.size() > this.maxCacheSize;
        }

    public static void main(String[] args) {
        LRUCache<String, String> lruCache = new LRUCache<>(12, 3);
        lruCache.put("1", "1");
        lruCache.put("2", "1");
        lruCache.put("3", "1");
        lruCache.put("4", "1");
        System.out.println(lruCache);
        lruCache.get("2");
        lruCache.put("5", "5");
        System.out.println(lruCache);



    }
}
