package com.abreaking.easyjpa.dao.prepare;


import com.abreaking.easyjpa.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 占位符sql的封装
 * @author liwei_paas
 * @date 2021/1/6
 */
public class PlaceholderMapper {

    private List<Fragment> sqlFragmentList = new ArrayList<>();
    // 占位符的参数即对应值
    private Map<String, Object> argsMap = new HashMap<>();
    // 实体类，可对占位符里的一些特殊参数进行映射，比如表名，主键名等
    private Set<Class> entitySet = new HashSet<>();

    public PlaceholderMapper() {
    }

    public PlaceholderMapper(String placeholderSql) {
        this.addSqlFragment(placeholderSql);
    }

    public PlaceholderMapper(String placeholderSql, Map<String, Object> argMap) {
        this(placeholderSql);
        this.argsMap = argMap;
    }

    public PlaceholderMapper(String placeholderSql, Object entity) {
        this(placeholderSql);
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
        this.addEntity(entity.getClass());
    }

    public void addSqlFragment(String sqlFragmentWithPlaceholder) {
        addSqlFragment(sqlFragmentWithPlaceholder,null,null);
    }

    public void addSqlFragment(String sqlFragmentWithPlaceholder,String argKey) {
        addSqlFragment(sqlFragmentWithPlaceholder,argKey,null);
    }

    public void addSqlFragment(String sqlFragmentWithPlaceholder,String argKey,Object rule){
        Fragment fragment = new Fragment();
        fragment.fragmentSql = sqlFragmentWithPlaceholder;
        fragment.argKey = argKey;
        fragment.rule = rule;
        sqlFragmentList.add(fragment);
    }

    public void addEntity(Class type){
        this.entitySet.add(type);
    }

    public void setArgsMap(Map<String, Object> argsMap) {
        this.argsMap = argsMap;
    }

    public List<Fragment> getSqlFragmentList() {
        return sqlFragmentList;
    }

    public Map<String, Object> getArgsMap() {
        return argsMap;
    }

    public Set<Class> getEntitySet() {
        return entitySet;
    }

    public String toPlaceholderSql(){
        StringBuilder builder = new StringBuilder();
        for (Fragment f : sqlFragmentList){
            builder.append(f.getFragmentSql());
            builder.append(" ");
        }
        return builder.toString();
    }

    /**
     * sql碎片，支持根据rule来决定sql是要还是不要
     */
    public static class Fragment{
        private String argKey;
        private String fragmentSql;
        private Object rule;

        public String getArgKey() {
            return argKey;
        }

        public String getFragmentSql() {
            return fragmentSql;
        }

        public Object getRule() {
            return rule;
        }
    }
}

