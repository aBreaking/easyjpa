package com.abreaking.easyjpa.sql;

import java.util.*;

/**
 * select 的sql构造器
 * 兼职sql优化的作用
 * @author liwei_paas
 * @date 2020/11/3
 */
public class SelectSqlBuilder implements SqlBuilder{

    private String tableName;

    private Set<String> statementSet = new HashSet<>();

    public SelectSqlBuilder() {
    }

    @Override
    public SqlBuilder table(String tableName) {
        this.tableName = tableName;
        return null;
    }

    @Override
    public SqlBuilder add(String columnName, String operator) {
        statementSet.add(columnName+" "+operator);
        return this;
    }

    @Override
    public String toSql() {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * from ");
        if (tableName == null){
            throw new RuntimeException("no table name");
        }
        sqlBuilder.append(tableName);
        if (statementSet.isEmpty()){
            return sqlBuilder.toString();
        }
        sqlBuilder.append(" WHERE 1=1 ");
        for (String statement : statementSet){
            sqlBuilder.append(" AND ").append(statement).append(" ?");
        }
        return sqlBuilder.toString();
    }
}
