package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.BaseEasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;


/**
 * insert
 * @author liwei_paas
 * @date 2020/12/15
 */
public class DeleteSqlBuilder extends AbstractSqlBuilder{

    @Override
    protected void doVisit(BaseEasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("DELETE FROM ");
        sqlBuilder.append(easyJpa.getTableName()).append(" ");

        SelectSqlBuilder selectSqlBuilder = new SelectSqlBuilder();
        selectSqlBuilder.where(sqlBuilder,columnMatrix,easyJpa);
    }


}
