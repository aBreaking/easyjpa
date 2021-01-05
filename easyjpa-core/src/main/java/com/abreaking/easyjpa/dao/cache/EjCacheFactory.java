package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.config.Configuration;

/**
 * 使用工厂模式来builder 获取缓存
 * @author liwei_paas
 * @date 2021/1/5
 */
public class EjCacheFactory {

    public static EjCache getDefaultCache(){
        String config = Configuration.cache.getConfig();
        if (config.equals("lru")){
            return new LruEjCache();
        }
        return new LruEjCache();
    }
}
