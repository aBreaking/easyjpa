package com.abreaking.easyjpa.dao.cache;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * 简单时间lru的缓存
 * @author liwei_paas
 * @date 2021/1/5
 */
public class LruEjCache  implements EjCache{


    /**
     * 最多有几个桶
     */
    private int maxBucketNumber = 16;

    /**
     * 每个桶最大容量
     * 目前该最大容量指每个buket的最大容量，应该考虑是总的cache最大容量
     */
    private int maxBucketSize = 32;

    /**
     * 缓存总的大小，后续应该将其智能，自动管理所有的桶
     */
    private int maxCacheSize = maxBucketSize * maxBucketNumber;

    /**
     * 缓存，也是一个链表，用于记录最早没用的cache
     */
    private Bucket<String,Bucket> cache ;

    /**
     * 读写锁
     */
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final Lock readLock = lock.readLock();
    final Lock writeLock = lock.writeLock();

    public LruEjCache() {
    }

    public LruEjCache(int maxBucketNumber, int maxBucketSize) {
        this.maxBucketNumber = maxBucketNumber;
        this.maxBucketSize = maxBucketSize;
        this.cache = new Bucket<>(maxBucketNumber);
        this.maxCacheSize = maxBucketSize * maxBucketNumber;
    }

    @Override
    public void hput(String bucket, CacheKey key, Object value) {
        if (bucket==null){
            throw new NullPointerException(bucket);
        }
        if (key == null){
            throw new NullPointerException();
        }
        Bucket b = cache.get(bucket);
        if (b==null){
            synchronized (cache){
                if ((b=cache.get(bucket))==null){ //双重校验，防止已经有线程添加了bucket
                    b = new Bucket(maxBucketSize);
                    lruPut(b,key,value);
                    cache.put(bucket,b);
                    return;
                }
            }
        }
        //使用写锁
        writeLock.lock();
        try {
            lruPut(b,key,value);
        }finally {
            writeLock.unlock();
        }

    }

    @Override
    public Object hget(String bucket, CacheKey key) {
        if (cache.containsKey(bucket)){
            return bucketGet(bucket,key);
        }else{
            synchronized (cache){
                if (cache.containsKey(bucket)){
                    return bucketGet(bucket,key);
                }
            }
        }
        return null;
    }

    @Override
    public void remove(String bucket) {
        if (cache.containsKey(bucket)){
            writeLock.lock();
            cache.remove(bucket);
            writeLock.unlock();
        }
    }

    @Override
    public Object hgetOrHputIfAbsent(String bucket, CacheKey key, Supplier supplier) {
        if (cache.containsKey(bucket)){
            Bucket b = cache.get(bucket);
            readLock.lock();
            try{
                if (!b.containsKey(key)){
                    writeLock.lock();
                    try{
                        if (!b.containsKey(key)){
                            Object value = supplier.get();
                            lruPut(b,key,value);
                            return value;
                        }
                    }finally {
                        writeLock.unlock();
                    }
                }
            }finally {
                readLock.unlock();
            }
        }else{
            synchronized (cache){
                if (!cache.containsKey(bucket)){
                    Object value = supplier.get();
                    lruPut(new Bucket(maxBucketSize),key,value);
                    return value;
                }
            }
        }
        return cache.get(bucket).get(key);
    }

    private Object bucketGet(String bucket,CacheKey key){
        Bucket b = cache.get(bucket);
        if (b==null){
            return null;
        }
        readLock.lock();
        try{
            return b.get(key);
        }finally {
            readLock.unlock();
        }
    }

    private void lruPut(Bucket bucket,CacheKey key , Object value){
        bucket.put(key,value);
    }

    @Override
    public String toString() {
        return "LruEjCache{" +
                "cache=" + cache +
                '}';
    }

    class Bucket<K,V> extends LinkedHashMap<K,V>{

        int maxBucketSize;

        public Bucket(int maxBucketSize) {
            super((int) Math.ceil(maxBucketSize / 0.75) + 1, 0.75f, true);
            this.maxBucketSize = maxBucketSize;
        }

        /**
         * 这里使用手动的方式删除，允许在其他处调用该bucket
         * @param eldest
         * @return
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size()>maxBucketSize;
        }

    }

}
