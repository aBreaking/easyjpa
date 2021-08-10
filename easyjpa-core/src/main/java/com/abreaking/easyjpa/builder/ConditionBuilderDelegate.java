package com.abreaking.easyjpa.builder;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.builder.dialect.DialectSqlBuilder;
import com.abreaking.easyjpa.util.SqlUtils;
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
        List<Condition> orderBy = conditions.getConditions(SqlConst.ORDER_BY);
        if (isBlank(orderBy)){
            return;
        }
        builder.append(" ORDER BY ");
        for (Condition condition : orderBy){
            builder.append(condition.getFcName()).append(" ");
            builder.append(condition.getValues()[0]).append(",");
        }
        StringUtils.cutAtLastSeparator(builder,",");
    }

    public void visitSelect(StringBuilder builder) {
        builder.append("SELECT ");
        List<Condition> list = this.conditions.getConditions(SqlConst.SELECT);
        if (isBlank(list)){
            builder.append("*");
        }else{
            Condition select = list.get(0);
            Object[] values = select.getValues();
            for (Object v : values){
                builder.append(v).append(",");
            }
            StringUtils.cutAtLastSeparator(builder,",");
        }
    }

    public void visitAnd(StringBuilder sqlBuilder,ColumnMatrix matrix){
        List<Condition> list = this.conditions.getConditions(SqlConst.AND);
        if (isBlank(list)){
            return;
        }
        sqlBuilder.append(" WHERE ");
        add(list,"AND",sqlBuilder,matrix);
    }

    public void visitWhere(StringBuilder sqlBuilder,ColumnMatrix matrix){
        List<Condition> and = conditions.getConditions(SqlConst.AND);
        List<Condition> or = conditions.getConditions(SqlConst.OR);

        if (isBlank(and) && isBlank(or)){
            return;
        }
        sqlBuilder.append(" WHERE ");
        if (!isBlank(and)){
            add(and,"AND",sqlBuilder,matrix);
        }
        if (!isBlank(or)){
            if (!isBlank(and)){
                sqlBuilder.append(" AND ");
            }
            sqlBuilder.append("(");
            add(or,"OR",sqlBuilder,matrix);
            sqlBuilder.append(")");
        }
    }

    private void add(List<Condition> conditions, String separator, StringBuilder sqlBuilder, ColumnMatrix matrix){
        for (Condition condition : conditions){
            String columnName = condition.getFcName();
            Object[] values = condition.getValues();
            String prepare = condition.getPrepare();
            if (prepare.indexOf("?")==-1){
                if (StringUtils.isNotEmpty(columnName)){
                    sqlBuilder.append(columnName).append(" ");
                }
                sqlBuilder.append(prepare);
            }else{
                if (StringUtils.isEmpty(columnName) || values.length == 0){
                    throw new EasyJpaException("every condition must specify column name and value");
                }
                Integer type = condition.getSqlType();
                if (type==null)type = SqlUtils.getSqlTypeByValue(values[0]);
                sqlBuilder.append(columnName).append(" ");
                sqlBuilder.append(condition.getPrepare()).append(" ");
                if (values.length==1){
                    matrix.put(columnName,type,values[0]);
                }else if (values.length>1){
                    for (int i = 0; i < values.length; i++) {
                        matrix.put(columnName+"_"+i,type,values[i]);
                    }
                }
            }
            sqlBuilder.append(separator).append(" ");
        }
        StringUtils.cutAtLastSeparator(sqlBuilder,separator);

    }

    public void visitPagination(StringBuilder sqlBuilder,ColumnMatrix columnMatrix) {
        List<Condition> limit = conditions.getConditions(SqlConst.LIMIT);
        if (isBlank(limit)){
            return;
        }
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

    public void visitGroupBy(StringBuilder builder) {
        List<Condition> groupBy = conditions.getConditions(SqlConst.GROUP_BY);
        if (isBlank(groupBy)){
            return;
        }
        builder.append(" GROUP BY ");
        for (Condition condition : groupBy){
            builder.append(condition.getFcName()).append(",");
        }
        StringUtils.cutAtLastSeparator(builder,",");
    }

    private boolean isBlank(List list){
        return list==null || list.isEmpty();
    }
}
