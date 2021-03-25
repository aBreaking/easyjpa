package com.abreaking.easyjpa.dao.cache;


/**
 * 一般使用表名作为bucket，bucket里则是具体的key - value
 * @author liwei_paas
 * @date 2021/1/5
 */
public interface EjCache {

    void hput(String bucket, SelectKey key, Object value);

    Object hget(String bucket,SelectKey key);

    void remove(String bucket);
}
