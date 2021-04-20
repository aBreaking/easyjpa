package com.abreaking.easyjpa.builder.prepare;


import com.abreaking.easyjpa.mapper.ClassMapper;
import com.abreaking.easyjpa.util.ReflectUtils;
import com.abreaking.easyjpa.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * 占位符sql的封装
 * @author liwei_paas
 * @date 2021/1/6
 */
public class PlaceholderWrapper {

    //sql占位符片段
    private List<Fragment> fragmentList = new LinkedList<>();

    // 占位符的参数即对应值
    private Map<String, Object> argMap = new HashMap<>();

    public PlaceholderWrapper() {
    }

    public PlaceholderWrapper(String placeholderSql) {
        append(placeholderSql);
    }

    public PlaceholderWrapper(String placeholderSql, Map<String, Object> argMap) {
        append(placeholderSql);
        this.argMap = argMap;
    }

    public void append(String placeholderSqlFragment){
        fragmentList.add(new Fragment(placeholderSqlFragment));
    }

    public void appendIfArgValueNotNull(String placeholderSqlFragment, String argKey){
        fragmentList.add(new Fragment(placeholderSqlFragment,map->argMap.containsKey(argKey) && argMap.get(argKey)!=null));
    }

    public void appendIfArgValueNotEmpty(String placeholderSqlFragment,String argKey){
        fragmentList.add(new Fragment(placeholderSqlFragment,argMap->{
            if (!argMap.containsKey(argKey)) return false;
            Object value = argMap.get(argKey);
            if (value==null) return false;
            if (value instanceof String){
                return StringUtils.isNotEmpty((String) value);
            }
            if (value instanceof Collection){
                Collection c = (Collection) value;
                return !c.isEmpty();
            }
            if (value instanceof Map){
                Map c = (Map) value;
                return !c.isEmpty();
            }
            return true;
        }));

    }

    public void appendIfArgNotEqualValue(String placeholderSqlFragment,String argKey,Object value){
        Object o = argMap.get(argKey);
        if (o!=null && !o.equals(value)){
            append(placeholderSqlFragment);
        }
    }

    public void addArgByEntity(Object entity){
        Map<String, Method> methodMap = ReflectUtils.poGetterMethodsMap(entity.getClass());
        for (String filedName : methodMap.keySet()){
            if (argMap.containsKey(filedName)){
                continue;
            }
            Method method = methodMap.get(filedName);
            try {
                Object value = method.invoke(entity);
                if (value!=null ){
                    argMap.put(filedName,value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
        addArgByClass(entity.getClass());
    }

    public void addArgByClass(Class obj){
        ClassMapper map = ClassMapper.map(obj);
        String name = obj.getSimpleName();
        argMap.putIfAbsent(name,map.mapTableName()); //类名也可以直接替换成表名
        argMap.putIfAbsent(name+".id",map.mapId().getFiledName()); //类名.id -> 主键名
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public Map<String, Object> getArgMap() {
        return argMap;
    }

    public void setArgMap(Map<String, Object> argMap) {
        this.argMap = argMap;
    }

    public static class Fragment{
        String fragmentSql; //sql片段
        Function<Map,Boolean> rule; //根据规则，要不要该sql片段

        public Fragment(String fragmentSql) {
            this.fragmentSql = fragmentSql;
            this.rule = a->true; //默认都要
        }

        public Fragment(String fragmentSql, Function<Map, Boolean> rule) {
            this.fragmentSql = fragmentSql;
            this.rule = rule;
        }

        public String getFragmentSql() {
            return fragmentSql;
        }
        public Function<Map, Boolean> getRule() {
            return rule;
        }
    }
}

