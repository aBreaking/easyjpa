package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.ReflectUtil;
import com.abreaking.easyjpa.util.SqlUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * prepareSql的默认实现
 * 它支持直接传入可执行的sql
 * @author liwei_paas
 * @date 2020/12/29
 */
public class PlaceHolderSqlBuilder extends AbstractSqlBuilder{

    private static final Pattern pattern = Pattern.compile("(\\$|#)\\{\\w+}");

    private static final String TABLE_NAME = "tableName";

    String placeholderSql;

    Map<String,Object> params ;

    public PlaceHolderSqlBuilder(String placeholderSql, Map<String,Object> params){
        this.placeholderSql = placeholderSql;
        this.params = params;
    }

    public PlaceHolderSqlBuilder(String placeholderSql,Object entity){
        this.placeholderSql = placeholderSql;
        this.params = new HashMap<>();
        Map<String, Method> methodMap = ReflectUtil.poGetterMethodsMap(entity.getClass());
        for (String filedName : methodMap.keySet()){
            Method method = methodMap.get(filedName);
            try {
                Object value = method.invoke(entity);
                if (value!=null){
                    params.put(filedName,value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
    }


    @Override
    protected void doVisit(EasyJpa easyJpa, ColumnMatrix columnMatrix) {
        this.fillParams(params,easyJpa);
        Matcher matcher = pattern.matcher(placeholderSql);
        int i = 0;
        while (matcher.find()){
            int start = matcher.start();
            sqlBuilder.append(placeholderSql, i, start);
            String group = matcher.group();
            String key = group.substring(2,group.length()-1);
            if (!params.containsKey(key)){

            }
            Object value = params.get(key);
            if (group.startsWith("${")){
                sqlBuilder.append(value);
            }else{
                sqlBuilder.append("?");
                columnMatrix.put(key,SqlUtil.getSqlType(value.getClass()),value);
            }
            i = matcher.end();
        }
        sqlBuilder.append(placeholderSql,i,placeholderSql.length());
    }

    /**
     * 将easyJpa里的部分映射内容，填充到params里面(params里不存在的)
     * @param params
     * @param easyJpa
     */
    public void fillParams(Map<String,Object> params,EasyJpa easyJpa){
        if (easyJpa == null){
            return;
        }
        if (!params.containsKey(TABLE_NAME)){
            params.put(TABLE_NAME,easyJpa.getTableName());
        }
        String idName = easyJpa.getIdName();
        if (params.containsKey("idName")){
            params.put("idName",idName);
        }
        String className = easyJpa.getObj().getSimpleName();
        if (!params.containsKey(className)){ // 支持通过 ${类名} 自动解析成表名
            params.put(className,easyJpa.getTableName());
        }
    }

}
