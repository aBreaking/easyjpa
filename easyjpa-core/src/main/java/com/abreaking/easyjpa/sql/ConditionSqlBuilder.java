package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.Condition;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;

import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author liwei_paas
 * @date 2020/12/25
 */
public class ConditionSqlBuilder {

    StringBuilder sqlBuilder;

    ColumnMatrix columnMatrix;

    public ConditionSqlBuilder(StringBuilder sqlBuilder, ColumnMatrix columnMatrix) {
        this.sqlBuilder = sqlBuilder;
        this.columnMatrix = columnMatrix;
    }

    /**
     * a=? and c like ? and e>?
     * @param list
     */
    protected void visitAnd(List<Condition> list) {
        // 去重,防止多个有相同的condition 以及 operator。保留最后加入condition
        HashMap<Condition,Object> map = new HashMap<>();
        for (Condition condition : list){
            if (map.containsKey(condition)){
                map.remove(condition,null);
            }
            map.put(condition,null);
        }
        prepareConditionSql(map.keySet(),"AND",null,null);
    }

    protected void visitOr(List<Condition> list) {
        prepareConditionSql(list,"OR",s->s.append("("),s->s.append(")"));
    }

    private void prepareConditionSql(Collection<Condition> list, String key, Consumer<StringBuilder> before, Consumer<StringBuilder> after){
        if (list==null || list.isEmpty()){
            return;
        }
        if (before!=null)before.accept(sqlBuilder);
        for (Condition condition : list){
            String fcName = condition.getFcName();
            if (fcName!=null){
                sqlBuilder.append(fcName).append(" ");
            }
            sqlBuilder.append(condition.getPrepare()).append(" ");
            sqlBuilder.append(key).append(" ");
            Object[] values = condition.getValues();
            for (Object value : values){
                columnMatrix.put(condition.getFcName(),condition.getSqlType(),value);
            }
        }
        sqlBuilder.replace(sqlBuilder.lastIndexOf(key),sqlBuilder.length()," ");
        if (after!=null)after.accept(sqlBuilder);
        sqlBuilder.append(" ");
    }

}
