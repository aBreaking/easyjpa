package com.abreaking.easyjpa.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * update的sql
 * @author liwei_paas
 * @date 2020/11/30
 */
public class UpdateSqlBuilder implements SqlBuilder{

    boolean where = false;

    String tableName;
    List<String> setList = new ArrayList<>();
    List<String> conditionList = new ArrayList<>();

    @Override
    public SqlBuilder table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    @Override
    public SqlBuilder add(String columnName, String operator) {
        if (!where){
            setList.add(columnName+"=?");
        }else{
            conditionList.add(columnName+operator+"?");
        }
        return this;
    }

    public SqlBuilder setWhere(boolean where){
        this.where = where;
        return this;
    }


    @Override
    public String toSql() {
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append(tableName).append(" SET ");
        for (String s : setList){
            builder.append(s);
            builder.append(",");
        }
        builder.append(" WHERE ");
        if (builder.lastIndexOf(",")!=-1){
            int index = builder.lastIndexOf(",");
            builder.replace(index,index+1,"");
        }
        for (String s : conditionList){
            builder.append(s).append(" AND");
        }
        return builder.lastIndexOf("AND")!=-1?builder.substring(0,builder.lastIndexOf("AND")):builder.toString();

    }
}
