package com.abreaking.easyjpa.builder;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.MatrixFactory;



/**
 * 查询语句
 * @author liwei_paas
 * @date 2020/12/15
 */
public class SelectSqlBuilder implements SqlBuilder{

    String table;

    public SelectSqlBuilder(String table) {
        this.table = table;
    }

    @Override
    public PreparedWrapper visit(Conditions conditions) {

        StringBuilder sqlBuilder = new StringBuilder();

        ColumnMatrix columnMatrix = MatrixFactory.createColumnMatrix();

        ConditionBuilderDelegate delegate = new ConditionBuilderDelegate(conditions);

        delegate.visitSelect(sqlBuilder);

        sqlBuilder.append(" FROM ").append(table).append(" ");

        delegate.visitWhere(sqlBuilder,columnMatrix);

        delegate.visitOrderBy(sqlBuilder);

        delegate.visitPagination(sqlBuilder,columnMatrix);

        return new PreparedWrapper(sqlBuilder.toString(),columnMatrix,table);
    }
}
