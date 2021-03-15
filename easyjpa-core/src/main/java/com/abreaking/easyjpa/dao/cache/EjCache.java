package com.abreaking.easyjpa.dao.cache;

import java.util.function.Supplier;


/**
 * 一般使用表名作为bucket，bucket里则是具体的key - value
 * @author liwei_paas
 * @date 2021/1/5
 */
public interface EjCache<V> {

    void hput(String bucket,CacheKey key,Object value);

    Object hget(String bucket,CacheKey key);

    void remove(String bucket);

    Object hgetOrHputIfAbsent(String bucket, CacheKey key, Supplier<Object> supplier);

}
