package com.abreaking.easyjpa.builder;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.StringUtils;


/**
 * insert
 * @author liwei_paas
 * @date 2020/12/15
 */
public class InsertSqlBuilder implements SqlBuilder{

    String table;

    Matrix matrix;

    public InsertSqlBuilder(String table, Matrix matrix2Insert) {
        this.table = table;
        this.matrix = matrix2Insert;
    }

    @Override
    public PreparedWrapper visit(Conditions conditions) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(table);
        sqlBuilder.append("(");
        String[] columns = matrix.columns();
        for (String  column : columns){
            sqlBuilder.append(column);
            sqlBuilder.append(",");
        }
        StringUtils.cutAtLastSeparator(sqlBuilder,",");
        sqlBuilder.append(") VALUES(");
        for (int i = 0; i < columns.length; i++) {
            sqlBuilder.append("?,");
        }
        StringUtils.cutAtLastSeparator(sqlBuilder,",");
        sqlBuilder.append(")");
        return new PreparedWrapper(sqlBuilder.toString(),matrix,table);
    }

}
