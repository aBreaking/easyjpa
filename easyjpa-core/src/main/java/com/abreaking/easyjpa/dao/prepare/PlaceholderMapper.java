package com.abreaking.easyjpa.dao.prepare;


import com.abreaking.easyjpa.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 占位符sql的封装
 * @author liwei_paas
 * @date 2021/1/6
 */
public class PlaceholderMapper {
    // 占位符sql
    private String placeholderSql;
    // 占位符的参数即对应值
    private Map<String, Object> argsMap = new HashMap<>();
    // 实体类，可对占位符里的一些特殊参数进行映射，比如表名，主键名等
    private Set<Class> entitySet = new HashSet<>();

    public PlaceholderMapper(String placeholderSql) {
        this.placeholderSql = placeholderSql;
    }

    public PlaceholderMapper(String placeholderSql, Map<String, Object> argMap) {
        this.placeholderSql = placeholderSql;
        this.argsMap = argMap;
    }

    public PlaceholderMapper(String placeholderSql, Object entity) {
        this.placeholderSql = placeholderSql;
        this.argsMap = new HashMap<>();
        Map<String, Method> methodMap = ReflectUtil.poGetterMethodsMap(entity.getClass());
        for (String filedName : methodMap.keySet()){
            Method method = methodMap.get(filedName);
            try {
                Object value = method.invoke(entity);
                if (value!=null){
                    argsMap.put(filedName,value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
        entitySet.add(entity.getClass());
    }

    public String getPlaceholderSql() {
        return placeholderSql;
    }

    public Map<String, Object> getArgsMap() {
        return argsMap;
    }

    public void setArgsMap(Map<String, Object> argsMap) {
        this.argsMap = argsMap;
    }

    public Set<Class> getEntitySet() {
        return entitySet;
    }

    public void addEntities(Class...entities) {
        for (Class c : entities){
            entitySet.add(c);
        }
    }
}

