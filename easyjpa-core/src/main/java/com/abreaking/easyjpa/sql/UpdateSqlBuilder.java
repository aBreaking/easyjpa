package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.BaseEasyJpa;
import com.abreaking.easyjpa.dao.Condition;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.SqlUtil;

import java.util.Collection;
import java.util.List;


/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class UpdateSqlBuilder extends AbstractSqlBuilder{

    Matrix conditionMatrix ;

    public UpdateSqlBuilder(Matrix conditionMatrix) {
        this.conditionMatrix = conditionMatrix;
    }

    public UpdateSqlBuilder() {
    }

    @Override
    protected void doVisit(BaseEasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("UPDATE ");
        sqlBuilder.append(easyJpa.getTableName());
        sqlBuilder.append(" SET ");
        Matrix matrix = easyJpa.matrix();
        String[] columns = conditionMatrix.columns();
        for (String column : columns){
            sqlBuilder.append(column);
            sqlBuilder.append("= ?,");
            columnMatrix.putAll(matrix);
        }
        cutLast(sqlBuilder,",");
        if (conditionMatrix==null){
            // 如果没有指定update的where条件，那么默认使用id作为条件
            conditionMatrix = easyJpa.idMatrix();
        }
        if (conditionMatrix!=null){
            sqlBuilder.append("WHERE ");
            for (int i = 0; i < columns.length; i++) {
                sqlBuilder.append(columns[i]);
                sqlBuilder.append("=? ");
            }
            columnMatrix.putAll(conditionMatrix);
        }
    }


}
