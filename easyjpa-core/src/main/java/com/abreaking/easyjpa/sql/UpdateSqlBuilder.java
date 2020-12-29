package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.StringUtils;


/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class UpdateSqlBuilder extends AbstractSqlBuilder{

    /**
     * update 的condition 条件
     */
    Conditions conditions ;

    public UpdateSqlBuilder(Conditions conditionList) {
        this.conditions = conditionList;
    }

    public UpdateSqlBuilder() {
    }

    @Override
    protected void doVisit(EasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("UPDATE ");
        sqlBuilder.append(easyJpa.getTableName());
        sqlBuilder.append(" SET ");
        Matrix matrix = easyJpa.matrix();
        String[] columns = matrix.columns();
        for (String column : columns){
            sqlBuilder.append(column);
            sqlBuilder.append("= ?,");
            columnMatrix.putAll(matrix);
        }
        StringUtils.cutAtLastSeparator(sqlBuilder,",");
        sqlBuilder.append(" ");
        if (conditions==null){
            // 如果没有指定update的where条件，那么默认使用id作为条件
            Matrix idMatrix = easyJpa.idMatrix();
            conditions = sqlConst -> sqlConst.equals(SqlConst.AND)?Condition.matrixToCondition(idMatrix):null;
        }
        if (conditions!=null){
            ConditionVisitor conditionVisitor = new ConditionVisitor(sqlBuilder, columnMatrix);
            conditionVisitor.visitWhere(conditions);
        }
    }


}
