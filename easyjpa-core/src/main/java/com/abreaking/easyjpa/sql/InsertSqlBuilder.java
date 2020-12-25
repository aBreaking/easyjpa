package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.BaseEasyJpa;
import com.abreaking.easyjpa.dao.Condition;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;

import java.util.Collection;
import java.util.List;


/**
 * insert
 * @author liwei_paas
 * @date 2020/12/15
 */
public class InsertSqlBuilder extends AbstractSqlBuilder{


    @Override
    protected void doVisit(BaseEasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("INSERT INTO ");
        sqlBuilder.append(easyJpa.getTableName());
        sqlBuilder.append("(");

        Matrix matrix = easyJpa.matrix();
        String[] columns = matrix.columns();
        for (String  column : columns){
            sqlBuilder.append(column);
            sqlBuilder.append(",");
        }
        cutLast(sqlBuilder,",");
        sqlBuilder.append(") VALUES(");
        for (int i = 0; i < columns.length; i++) {
            sqlBuilder.append("?,");
        }
        cutLast(sqlBuilder,",");
        sqlBuilder.append(")");
        columnMatrix.putAll(matrix);
    }


}
