package com.abreaking.easyjpa.dao.prepare;


import com.abreaking.easyjpa.mapper.ClassMapper;
import com.abreaking.easyjpa.util.ReflectUtil;
import com.abreaking.easyjpa.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 占位符sql的封装
 * @author liwei_paas
 * @date 2021/1/6
 */
public class PlaceholderMapper{

    //sql占位符片段
    private StringBuilder placeholderSqlBuilder = new StringBuilder();

    // 占位符的参数即对应值
    private Map<String, Object> argMap = new HashMap<>();


    public PlaceholderMapper(String placeholderSql) {
        placeholderSqlBuilder.append(placeholderSql);
    }

    public PlaceholderMapper() {
    }

    public PlaceholderMapper(String placeholderSql, Map<String, Object> argMap) {
        placeholderSqlBuilder.append(placeholderSql);
        this.argMap = argMap;
    }

    public PlaceholderMapper(String placeholderSql, Object entity) {
        this(placeholderSql);
        this.addArgByEntity(entity);
    }

    public void append(String placeholderSqlFragment){
        placeholderSqlBuilder.append(placeholderSqlFragment);
    }

    public void appendIfArgNotNull(String placeholderSqlFragment,String key){
        if (argMap.containsKey(key) && argMap.get(key)!=null){
            placeholderSqlBuilder.append(placeholderSqlFragment);
        }
    }

    public void appendIfArgNotEmpty(String placeholderSqlFragment,String key){
        if (!argMap.containsKey(key)){
            return;
        }
        Object value = argMap.get(key);
        if (value==null)return;
        if (value instanceof String){
            if (StringUtils.isNotEmpty((String) value)){
                append(placeholderSqlFragment);
                return;
            }
        }
        if (value instanceof Collection){
            Collection c = (Collection) value;
            if (!c.isEmpty()){
                append(placeholderSqlFragment);
                return;
            }
        }
        if (value instanceof Map){
            Map c = (Map) value;
            if (!c.isEmpty()){
                append(placeholderSqlFragment);
                return;
            }
        }
    }

    public void appendIfArgNotEqualValue(String placeholderSqlFragment,String key,Object value){
        Object o = argMap.get(key);
        if (o!=null && !o.equals(value)){
            append(placeholderSqlFragment);
        }
    }

    public void addArgByEntity(Object entity){
        Map<String, Method> methodMap = ReflectUtil.poGetterMethodsMap(entity.getClass());
        for (String filedName : methodMap.keySet()){
            Method method = methodMap.get(filedName);
            try {
                Object value = method.invoke(entity);
                if (value!=null){
                    argMap.put(filedName,value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
        Class<?> obj = entity.getClass();
        ClassMapper map = ClassMapper.map(obj);
        argMap.put(obj.getSimpleName(),map.mapTableName()); //类名也可以直接替换成表名
    }

    public StringBuilder getPlaceholderSqlBuilder() {
        return placeholderSqlBuilder;
    }

    public Map<String, Object> getArgMap() {
        return argMap;
    }

    public void setPlaceholderSqlBuilder(StringBuilder placeholderSqlBuilder) {
        this.placeholderSqlBuilder = placeholderSqlBuilder;
    }

    public void setArgMap(Map<String, Object> argMap) {
        this.argMap = argMap;
    }
}

