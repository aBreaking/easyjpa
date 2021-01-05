package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.util.function.Supplier;


/**
 * 缓存工具
 * 底层一般会用map会实现, 与普通的Map不同的是，它加入了一个salt ,salt看作另一种key，也就是同一个key在不同的salt下value也会不一样，即key+salt -> value
 * @author liwei_paas
 * @date 2021/1/5
 */
public interface EjCache<V> {

    void put(EasyJpa key, RowMapper rowMapper, V value);

    V get(EasyJpa key, RowMapper rowMapper);

    V remove(String tableName);

    V remove(EasyJpa tableName);

    default V getIfAbsent(EasyJpa key, RowMapper rowMapper, Supplier<V> supplier){
        V ret = get(key,rowMapper);
        if (ret==null){
            ret = supplier.get();
            put(key,rowMapper,ret);
        }
        return ret;
    }

}
