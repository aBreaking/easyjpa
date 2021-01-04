package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;


/**
 * 查询语句
 * @author liwei_paas
 * @date 2020/12/15
 */
public class SelectSqlBuilder extends AbstractSqlBuilder{

    @Override
    protected void doVisit(EasyJpa easyJpa,ColumnMatrix matrix) {

        visitSelect(easyJpa);

        sqlBuilder.append("FROM ").append(easyJpa.getTableName()).append(" ");

        visitWhere(easyJpa,matrix);

        visitOrderBy(easyJpa);

        visitPagination(easyJpa,matrix);

    }

}
