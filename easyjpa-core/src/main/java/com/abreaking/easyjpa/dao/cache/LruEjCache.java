package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.config.Configuration;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.util.*;

/**
 * 简单时间lru的缓存
 * @author liwei_paas
 * @date 2021/1/5
 */
public class LruEjCache extends LinkedHashMap<String,Map> implements EjCache {

    private static final int MAX_CACHE_SIZE = Integer.parseInt(Configuration.cache_lru_max_size.getConfig());


    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size()>MAX_CACHE_SIZE;
    }

    @Override
    public void put(EasyJpa easyJpa, RowMapper rowMapper, Object value) {
        String key = easyJpa.getTableName();
        Map<String,Object> valueMap ;
        if (containsKey(key)){
            valueMap = (Map<String, Object>) get(key);
        }else{
            valueMap = new HashMap<>();
            put(key,valueMap);
        }
        valueMap.put(ejKey(easyJpa,rowMapper),value);
    }

    @Override
    public Object get(EasyJpa easyJpa, RowMapper rowMapper) {
        Map<String,Object> valueMap = (Map<String, Object>) get(easyJpa.getTableName());
        if (valueMap!=null){
            return valueMap.get(ejKey(easyJpa,rowMapper));
        }
        return null;
    }

    @Override
    public Object remove(String key) {
        return super.remove(key);
    }

    @Override
    public Object remove(EasyJpa easyJpa) {
        return super.remove(easyJpa.getTableName());
    }

    private String ejKey(EasyJpa easyJpa,RowMapper rowMapper){
        return String.valueOf(easyJpa.hashCode())+rowMapper.getClass().getName();
    }

}
