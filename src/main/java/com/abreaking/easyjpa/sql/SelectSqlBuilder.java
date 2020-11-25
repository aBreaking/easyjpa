package com.abreaking.easyjpa.sql;

import java.util.Map;
import java.util.TreeMap;

/**
 * select 的sql构造器
 * @author liwei_paas
 * @date 2020/11/3
 */
public class SelectSqlBuilder implements SqlBuilder{

    private Map<String,String> map = new TreeMap<>();

    private StringBuilder sqlBuilder = new StringBuilder("SELECT * from ");

    public SelectSqlBuilder(String tableName) {
        sqlBuilder.append(tableName);
        sqlBuilder.append(" WHERE 1=1 ");
    }

    @Override
    public SqlBuilder add(String columnName, String operator) {
        sqlBuilder.append(" AND ")
                .append(columnName)
                .append(" ")
                .append(operator)
                .append(" ?");
        return this;
    }

    @Override
    public String toSql() {
        return sqlBuilder.toString();
    }
}
