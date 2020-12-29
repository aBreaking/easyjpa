package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;


/**
 * insert
 * @author liwei_paas
 * @date 2020/12/15
 */
public class DeleteSqlBuilder extends AbstractSqlBuilder{

    @Override
    protected void doVisit(EasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("DELETE FROM ");
        sqlBuilder.append(easyJpa.getTableName()).append(" ");

        ConditionVisitor conditionVisitor = new ConditionVisitor(sqlBuilder,columnMatrix);
        conditionVisitor.visitWhere(easyJpa);
    }

}
