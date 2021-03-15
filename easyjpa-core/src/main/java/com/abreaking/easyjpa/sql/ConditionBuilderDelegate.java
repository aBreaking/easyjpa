package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.StringUtils;

import java.util.List;

/**
 * 构造条件的委托
 * @author liwei
 * @date 2021/2/25
 */
public class ConditionBuilderDelegate {

    private Conditions conditions;

    public ConditionBuilderDelegate(Conditions conditions) {
        this.conditions = conditions;
    }

    public void visitOrderBy(StringBuilder builder){

    }

    public void visitSelect(StringBuilder builder) {
        if (conditions.isEmpty(SqlConst.SELECT)){
            builder.append("SELECT *");
        }else{
            conditions.getConditions(SqlConst.SELECT).forEach(i->builder.append(i).append(","));
            StringUtils.cutAtLastSeparator(builder,",");
        }
    }

    public void visitAnd(StringBuilder sqlBuilder,ColumnMatrix matrix){
        if (!conditions.isEmpty(SqlConst.AND)){
            sqlBuilder.append(" WHERE ");
            add(conditions.getConditions(SqlConst.AND),"AND",sqlBuilder,matrix);
        }
    }

    public void visitWhere(StringBuilder sqlBuilder,ColumnMatrix matrix){

        if (!(conditions.isEmpty(SqlConst.AND) || conditions.isEmpty(SqlConst.OR))){
            sqlBuilder.append(" WHERE ");
        }
        if (!conditions.isEmpty(SqlConst.AND)){
            add(conditions.getConditions(SqlConst.AND),"AND",sqlBuilder,matrix);
        }
        if (!conditions.isEmpty(SqlConst.OR)){
            if (!conditions.isEmpty(SqlConst.AND)){
                sqlBuilder.append(" AND ");
            }
            sqlBuilder.append("(");
            add(conditions.getConditions(SqlConst.OR),"OR",sqlBuilder,matrix);
            sqlBuilder.append(")");
        }
    }

    private void add(List<Condition> conditions, String separator, StringBuilder sqlBuilder, ColumnMatrix matrix){

        for (Condition condition : conditions){
            sqlBuilder.append(condition.getPrepare()).append(" ").append(separator).append(" ");
            Object[] values = condition.getValues();
            if (values.length==1){
                matrix.put(condition.getFcName(),condition.getSqlType(),values[0]);
            }else if (values.length>1){
                for (int i = 0; i < values.length; i++) {
                    matrix.put(condition.getFcName()+"_"+i,condition.getSqlType(),values[i]);
                }
            }
        }
        StringUtils.cutAtLastSeparator(sqlBuilder,separator);

    }

    public void visitPagination(StringBuilder sqlBuilder) {

    }
}
