package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.config.Configuration;
import com.abreaking.easyjpa.executor.ConnectionHolder;
import com.abreaking.easyjpa.executor.SqlExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用工厂模式来builder 获取缓存
 * @author liwei_paas
 * @date 2021/1/5
 */
public class EjCacheFactory {

    private static final Map<String,EjCache> CACHE_MAP = new HashMap<>();

    public static EjCache getDefaultCache(SqlExecutor sqlExecutor){
        String key = key(sqlExecutor);
        if (!CACHE_MAP.containsKey(key)){
            synchronized (CACHE_MAP){
                if (!CACHE_MAP.containsKey(key)){
                    CACHE_MAP.put(key,null);
                }
            }
        }
        return CACHE_MAP.get(key);
    }

    private static String key(SqlExecutor sqlExecutor){
        ConnectionHolder holder = sqlExecutor.getConnectionHolder();
        if (holder != null){
            String keyPrefix = sqlExecutor.getClass().getName();
            return keyPrefix+holder.getJdbcUrl()+holder.getJdbcUserName()+holder.getJdbcDriverName();
        }
        return sqlExecutor.toString();


    }

}
