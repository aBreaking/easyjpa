package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;
import com.abreaking.easyjpa.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * 条件的sql构造器，它是一个工具类，用于visit sql的各个关键字，进而组装成prepare的sql片段
 * @author liwei_paas
 * @date 2020/12/25
 */
public class ConditionVisitor {

    StringBuilder sqlBuilder;

    ColumnMatrix columnMatrix;

    public ConditionVisitor(StringBuilder sqlBuilder, ColumnMatrix columnMatrix) {
        this.sqlBuilder = sqlBuilder;
        this.columnMatrix = columnMatrix;
    }

    /**
     * 来访问sql的某个关键字，而后将组装后的sql片段 放在sqlBuilder里面。
     * 它抽象了visit的通用方法，具体的visit细节由各个实际方法去实现
     * @param conditions
     * @param sqlConst
     * @param consumer
     * @return 有sql片段放在了sqlBuilder里 即为true，反之为false
     */
    protected boolean visit(Conditions conditions, SqlConst sqlConst, Consumer<List<Condition>> consumer){
        List<Condition> list = conditions.getConditions(sqlConst);
        if (list==null || list.isEmpty()){
            return false;
        }
        consumer.accept(list);
        return true;
    }

    /**
     * 将conditions 解析成这种格式：a=? and c like ? and e>?
     * @param
     */
    public boolean visitAnd(Conditions conditions) {
        return visit(conditions,SqlConst.AND,list->{
            // 先去重,防止多个有相同的condition 以及 operator。保留最后加入condition
            HashMap<Condition,Object> map = new HashMap<>();
            for (Condition condition : list){
                if (map.containsKey(condition)){
                    map.remove(condition,null);
                }
                map.put(condition,null);
            }
            prepareConditionSql(map.keySet(),"AND",null,null);
        });
    }

    public boolean visitOr(Conditions conditions) {
        return visit(conditions,SqlConst.OR,list->prepareConditionSql(list,"OR",s->s.append("("),s->s.append(")")));
    }

    public boolean visitWhere(Conditions conditions){
        sqlBuilder.append("WHERE ");
        boolean and = visitAnd(conditions);
        if (and){
            sqlBuilder.append("AND ");
        }
        boolean or = visitOr(conditions);
        if (!or && !and){ //or and 都没有
            //去掉 "where"
            StringUtils.cutAtLastSeparator(sqlBuilder,"WHERE");
            return false;
        }
        if (!or){ // 没有or，此时只有and. 去掉and加上的 "AND"
            StringUtils.cutAtLastSeparator(sqlBuilder,"AND");
            return false;
        }
        return true;
    }

    public boolean visitOrderBy(Conditions conditions){
        return visit(conditions,SqlConst.ORDER_BY,orderBy->{
            sqlBuilder.append("ORDER BY ");
            for (Condition condition : orderBy){
                sqlBuilder.append(condition.getFcName());
                sqlBuilder.append(" ");
                sqlBuilder.append(condition.getValues()[0]);
                sqlBuilder.append(",");
            }
            StringUtils.cutAtLastSeparator(sqlBuilder,",");
            sqlBuilder.append(" ");
        });
    }

    public boolean visitLimit(Conditions conditions){
        return visit(conditions,SqlConst.LIMIT,limit->{
            Condition condition = limit.get(0);
            sqlBuilder.append("LIMIT ?,? ");
            Object[] values = condition.getValues();
            columnMatrix.put("start",SqlUtil.getSqlType(Integer.class),values[0]);
            columnMatrix.put("end",SqlUtil.getSqlType(Integer.class),values[1]);
        });
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
