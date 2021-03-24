package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.executor.ConnectionHolder;
import com.abreaking.easyjpa.executor.ConnectionWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用工厂模式来builder 获取缓存
 * @author liwei_paas
 * @date 2021/1/5
 */
public class EjCacheFactory {

    private static final Map<ConnectionWrapper,EjCache> CACHE_MAP = new HashMap<>();

    public static EjCache getLocalDefaultCache(){
        ConnectionWrapper connectionWrapper = ConnectionHolder.getLocalConnection();
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
