package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.sql.dialect.DialectSqlBuilder;
import com.abreaking.easyjpa.support.EasyJpa;
import com.abreaking.easyjpa.util.SqlUtil;
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
        if (conditions.isEmpty(SqlConst.ORDER_BY)){
            return;
        }
        List<Condition> orderBy = conditions.getConditions(SqlConst.ORDER_BY);
        builder.append(" ORDER BY ");
        for (Condition condition : orderBy){
            builder.append(condition.getFcName()).append(" ");
            builder.append(condition.getValues()[0]).append(",");
        }
        StringUtils.cutAtLastSeparator(builder,",");
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
        if (conditions.isEmpty(SqlConst.AND)){
            return;
        }
        sqlBuilder.append(" WHERE ");
        add(conditions.getConditions(SqlConst.AND),"AND",sqlBuilder,matrix);
    }

    public void visitWhere(StringBuilder sqlBuilder,ColumnMatrix matrix){
        if (conditions.isEmpty(SqlConst.AND) && conditions.isEmpty(SqlConst.OR)){
            return;
        }

        sqlBuilder.append(" WHERE ");

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
            String columnName = condition.getFcName();
            Object[] values = condition.getValues();
            if (StringUtils.isEmpty(columnName) || values.length == 0){
                throw new EasyJpaException("every condition must specify column name and value");
            }
            Integer type = condition.getSqlType();
            if (type==null)type = SqlUtil.getSqlTypeByValue(values[0]);
            sqlBuilder.append(columnName).append(" ");
            sqlBuilder.append(condition.getPrepare()).append(" ");
            sqlBuilder.append(separator).append(" ");
            if (values.length==1){
                matrix.put(columnName,type,values[0]);
            }else if (values.length>1){
                for (int i = 0; i < values.length; i++) {
                    matrix.put(columnName+"_"+i,type,values[i]);
                }
            }
        }
        StringUtils.cutAtLastSeparator(sqlBuilder,separator);

    }

    public void visitPagination(StringBuilder sqlBuilder,ColumnMatrix columnMatrix) {
        if (conditions.isEmpty(SqlConst.LIMIT)){
            return;
        }
        List<Condition> limit = conditions.getConditions(SqlConst.LIMIT);
        Condition condition = limit.get(0);
        Object[] values = condition.getValues();
        if (values==null || values.length==0){
            return;
        }
        int pageStartIndex = 0;
        int pageSize = 0;
        if (values.length==1){
            pageSize  = (Integer) values[0];
        }else if (values.length>1){
            pageStartIndex = (Integer) values[0];
            pageSize = (Integer) values[1];
        }
        DialectSqlBuilder defaultDialectSqlBuilder = DialectSqlBuilder.getDefaultDialectSqlBuilder();
        defaultDialectSqlBuilder.visitPage(sqlBuilder,columnMatrix,pageStartIndex,pageSize);
    }

}
