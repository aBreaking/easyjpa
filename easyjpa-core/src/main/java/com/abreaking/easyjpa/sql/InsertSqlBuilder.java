package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.support.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.StringUtils;


/**
 * insert
 * @author liwei_paas
 * @date 2020/12/15
 */
public class InsertSqlBuilder extends AbstractSqlBuilder{

    @Override
    protected void doVisit(EasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("INSERT INTO ");
        sqlBuilder.append(easyJpa.getTableName());
        sqlBuilder.append("(");

        Matrix matrix = easyJpa.matrix();
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
        columnMatrix.putAll(matrix);
    }


}
