package com.abreaking.easyjpa.sql;

/**
 * @{USER}
 * @{DATE}
 */
public class DeleteSqlBuilder implements SqlBuilder{

    StringBuilder builder = new StringBuilder("DELETE FROM ");

    @Override
    public SqlBuilder table(String tableName) {
        builder.append(tableName);
        builder.append(" WHERE 1=1 ");
        return this;
    }

    @Override
    public SqlBuilder add(String columnName, String operator) {
        builder.append(" AND ").append(columnName).append(operator).append("?");
        return this;
    }

    @Override
    public String toSql() {
        return builder.toString();
    }
}
