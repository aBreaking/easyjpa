package com.abreaking.easyjpa.builder;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;


/**
 * delete 删除语句
 * @author liwei_paas
 * @date 2020/12/15
 */
public class DeleteSqlBuilder implements SqlBuilder{

    String table;

    public DeleteSqlBuilder(String table) {
        this.table = table;
    }

    @Override
    public PreparedWrapper visit(Conditions conditions) {
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ");
        ColumnMatrix matrix = new AxisColumnMatrix();
        sqlBuilder.append(table);
        ConditionBuilderDelegate delegate = new ConditionBuilderDelegate(conditions);
        delegate.visitWhere(sqlBuilder,matrix);
        return new PreparedWrapper(sqlBuilder.toString(),matrix);
    }
}
