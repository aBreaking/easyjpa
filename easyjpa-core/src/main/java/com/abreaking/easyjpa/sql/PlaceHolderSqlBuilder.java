package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;

import java.util.Map;
import java.util.Set;
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


    PlaceholderMapper placeholderMapper;

    public PlaceHolderSqlBuilder(PlaceholderMapper placeholderMapper){
        this.placeholderMapper = placeholderMapper;
    }

    @Override
    protected void doVisit(EasyJpa easyJpa, ColumnMatrix columnMatrix) {
        String placeholderSql = placeholderMapper.getPlaceholderSql();
        Map<String, Object> argsMap = placeholderMapper.getArgsMap();
        Set<Class> entitySet = placeholderMapper.getEntitySet();

        if(argsMap.isEmpty() || entitySet.isEmpty()){
            sqlBuilder.append(placeholderSql);
            return;
        }

        if (easyJpa != null){
            fillParams(argsMap,easyJpa);
        }
        for (Class entity : entitySet){
            fillParams(argsMap,new EasyJpa(entity));
        }

        Matcher matcher = pattern.matcher(placeholderSql);
        int i = 0;
        while (matcher.find()){
            int start = matcher.start();
            sqlBuilder.append(placeholderSql, i, start);
            String group = matcher.group();
            String key = group.substring(2,group.length()-1);
            if (!argsMap.containsKey(key)){

            }
            Object value = argsMap.get(key);
            if (group.startsWith("${")){
                sqlBuilder.append(value);
            }else{
                sqlBuilder.append("?");
                columnMatrix.put(key,SqlUtil.getSoftSqlType(value.getClass()),value);
            }
            i = matcher.end();
        }
        sqlBuilder.append(placeholderSql,i,placeholderSql.length());
    }

    /**
     * 将easyJpa里的部分映射内容，填充到params里面(params里不存在的)
     * 也应该可以直接字段名的补充的。后续再实现把
     * @param params
     * @param easyJpa
     */
    public void fillParams(Map<String,Object> params,EasyJpa easyJpa){
        params.putIfAbsent("tableName",easyJpa.getTableName());
        params.putIfAbsent("tablename".toLowerCase(),easyJpa.getTableName());
        params.put(easyJpa.getObj().getSimpleName(),easyJpa.getTableName()); //类名也可以直接替换成表名
        params.put("idName",easyJpa.getIdName());
    }

}
