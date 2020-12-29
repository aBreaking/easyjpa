package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.StringUtils;


/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class SelectSqlBuilder extends AbstractSqlBuilder{

    @Override
    protected void doVisit(EasyJpa easyJpa,ColumnMatrix matrix) {
        ConditionVisitor conditionVisitor = new ConditionVisitor(sqlBuilder, matrix);

        sqlBuilder.append("SELECT ");
        boolean select = conditionVisitor.visit(easyJpa, SqlConst.SELECT, selects -> {
            Condition condition = selects.get(0);
            Object[] values = condition.getValues();
            for (Object value : values) {
                sqlBuilder.append(value).append(",");
            }
            StringUtils.cutAtLastSeparator(sqlBuilder, ",");
            sqlBuilder.append(" ");
        });
        if (!select){
            sqlBuilder.append("* ");
        }

        sqlBuilder.append("FROM ");
        sqlBuilder.append(easyJpa.getTableName()).append(" ");

        conditionVisitor.visitWhere(easyJpa);

        conditionVisitor.visitOrderBy(easyJpa);

        conditionVisitor.visitLimit(easyJpa);

    }

}
