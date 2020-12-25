package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.BaseEasyJpa;
import com.abreaking.easyjpa.dao.Condition;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;

import java.util.List;


/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class SelectSqlBuilder extends AbstractSqlBuilder{

    @Override
    protected void doVisit(BaseEasyJpa easyJpa,ColumnMatrix matrix) {
        sqlBuilder.append("SELECT ");
        List<Condition> conditions = easyJpa.getConditions(SqlConst.SELECT);
        if (conditions!=null){
            Condition condition = conditions.get(0);
            Object[] values = condition.getValues();
            for (Object select : values){
                sqlBuilder.append(select).append(",");
            }
            cutLast(sqlBuilder,",");
        }else{
            sqlBuilder.append("* ");
        }
        sqlBuilder.append("FROM ");
        sqlBuilder.append(easyJpa.getTableName()).append(" ");

        where(sqlBuilder,matrix,easyJpa);

        List<Condition> orderBy = easyJpa.getConditions(SqlConst.ORDER_BY);
        if (orderBy!=null){
            sqlBuilder.append("ORDER BY ");
            for (Condition condition : orderBy){
                sqlBuilder.append(condition.getFcName());
                sqlBuilder.append(" ");
                sqlBuilder.append(condition.getValues()[0]);
                sqlBuilder.append(",");
            }
            cutLast(sqlBuilder,",");
        }

        List<Condition> limit = easyJpa.getConditions(SqlConst.LIMIT);
        if (limit!=null){
            Condition condition = limit.get(0);
            sqlBuilder.append("LIMIT ?,? ");
            Object[] values = condition.getValues();
            matrix.put("start",SqlUtil.getSqlType(Integer.class),values[0]);
            matrix.put("end",SqlUtil.getSqlType(Integer.class),values[1]);
        }
    }

    protected void where(StringBuilder sqlBuilder,ColumnMatrix matrix,BaseEasyJpa easyJpa){
        List<Condition> and = easyJpa.getConditions(SqlConst.AND);
        if (and!=null){
            sqlBuilder.append("WHERE ");
            ConditionSqlBuilder conditionSqlBuilder = new ConditionSqlBuilder(sqlBuilder, matrix);
            conditionSqlBuilder.visitAnd(and);
        }
        List<Condition> or = easyJpa.getConditions(SqlConst.OR);
        if (or!=null){
            if (and==null){
                sqlBuilder.append("WHERE ");
            }else{
                sqlBuilder.append("AND ");
            }
            ConditionSqlBuilder conditionSqlBuilder = new ConditionSqlBuilder(sqlBuilder, matrix);
            conditionSqlBuilder.visitOr(or);
        }

    }

}
