package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.executor.ConnectionHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用工厂模式来builder 获取缓存
 * @author liwei_paas
 * @date 2021/1/5
 */
public class EjCacheFactory {

    /**
     * cache缓存，get 用了dcl
     */
    private static final Map<ConnectionHolder,EjCache> CACHE_MAP = new HashMap<>();

    public static EjCache getLocalDefaultCache(){
        ConnectionHolder connectionWrapper = ConnectionHolder.getLocalConnection();
        if (!CACHE_MAP.containsKey(connectionWrapper)){
            synchronized (CACHE_MAP){
                if (!CACHE_MAP.containsKey(connectionWrapper)){
                    CACHE_MAP.put(connectionWrapper,new LruEjCache());
                }
            }
        }
        return CACHE_MAP.get(connectionWrapper);
    }
}
