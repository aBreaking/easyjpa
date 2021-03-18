package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.mapper.matrix.MatrixFactory;
import com.abreaking.easyjpa.util.StringUtils;


/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class UpdateSqlBuilder implements SqlBuilder{

    String table;

    Matrix matrix;

    public UpdateSqlBuilder(String table, Matrix matrix) {
        this.table = table;
        this.matrix = matrix;
    }

    @Override
    public PreparedWrapper visit(Conditions conditions) {
        String[] columns = matrix.columns();

        StringBuilder sqlBuilder = new StringBuilder();
        ColumnMatrix columnMatrix = MatrixFactory.createColumnMatrix(columns.length);
        sqlBuilder.append("UPDATE ");
        sqlBuilder.append(table);
        sqlBuilder.append(" SET ");
        for (String column : columns){
            sqlBuilder.append(column);
            sqlBuilder.append("= ?,");
        }
        columnMatrix.putAll(matrix);

        StringUtils.cutAtLastSeparator(sqlBuilder,",");

        ConditionBuilderDelegate delegate = new ConditionBuilderDelegate(conditions);

        delegate.visitWhere(sqlBuilder,columnMatrix);

        return new PreparedWrapper(sqlBuilder.toString(),columnMatrix);
    }

}
