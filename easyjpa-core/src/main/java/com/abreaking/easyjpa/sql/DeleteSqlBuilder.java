package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.support.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;


/**
 * delete 删除语句
 * @author liwei_paas
 * @date 2020/12/15
 */
public class DeleteSqlBuilder extends AbstractSqlBuilder{

    @Override
    protected void doVisit(EasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("DELETE FROM ");
        sqlBuilder.append(easyJpa.getTableName()).append(" ");
        visitWhere(easyJpa,columnMatrix);
    }

}
