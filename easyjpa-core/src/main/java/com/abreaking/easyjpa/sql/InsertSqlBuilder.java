package com.abreaking.easyjpa.sql;

/**
 * @{USER}
 * @{DATE}
 */
public class InsertSqlBuilder implements SqlBuilder{

    StringBuilder builder = new StringBuilder("INSERT INTO ");
    int c = 0;
    @Override
    public SqlBuilder table(String tableName) {
        builder.append(tableName);
        builder.append("(");
        return this;
    }

    @Override
    public SqlBuilder add(String columnName, String operator) {
        builder.append(columnName);
        builder.append(",");
        c++;
        return this;
    }

    @Override
    public String toSql() {
        if (builder.lastIndexOf(",")!=-1){
            int index = builder.lastIndexOf(",");
            builder.replace(index,index+1,"");
        }
        builder.append(") VALUES(");
        for (int i = 0; i < c; i++) {
            builder.append("?").append(",");
        }
        if (c>0){
            int index = builder.lastIndexOf(",");
            builder.replace(index,index+1,"");
        }
        builder.append(")");
        return builder.toString();
    }
}
