package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.dao.prepare.PreparedMapper;
import com.abreaking.easyjpa.exception.NoSuchFieldOrColumnException;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.dao.condition.SqlConst;

import java.util.*;

/**
 * 我们用easyJpa来完成单个对象（表） 增删改查的条件组装操作。
 * @author liwei_paas
 * @date 2020/11/13
 */
public final class EasyJpa<T> extends EasyJpaMapper implements Conditions {

    private Map<SqlConst,List<Condition>> conditionMap = new HashMap<>();

    public EasyJpa(T o) {
        super(o);
        conditionMap.put(SqlConst.AND,Condition.matrixToCondition(super.matrix));
    }

    public EasyJpa(Class obj) {
        super(obj);
    }

    public void select(String...fcNames){
        String[] columnNames = new String[fcNames.length];
        for (int i = 0; i < fcNames.length; i++) {
            FieldMapper mapper = classMapper.mapField(fcNames[i]);
            columnNames[i] = mapper == null?fcNames[i]:mapper.getColumnName();
        }
        addCondition(SqlConst.SELECT,Condition.prepare("SELECT",columnNames),true);
    }

    public void and(Condition condition){
        addCondition(SqlConst.AND,condition);
    }

    public void and(String fcNameWithOperator,Object value){
        fcNameWithOperator(SqlConst.AND,fcNameWithOperator,value);
    }

    public void or(String fcNameWithOperator,Object...values){
        fcNameWithOperator(SqlConst.OR,fcNameWithOperator,values);
    }

    public void or(Condition condition){
        addCondition(SqlConst.OR,condition);
    }

    public void orderBy(String fcName,Boolean asc){
        addCondition(SqlConst.ORDER_BY,Condition.to(fcName," ORDER BY ",asc?"ASC":"DESC"));
    }

    public void limit(int start,int offset){
        addCondition(SqlConst.LIMIT,Condition.prepare("limit ?,?",start,offset),true);
    }

    public static PreparedMapper buildPrepared(String prepareSql, Object...values){
        return new PreparedMapper(prepareSql,values);
    }
    public static PlaceholderMapper buildPlaceholder(String placeholderSql, Map<String,Object> argsMap){
        return new PlaceholderMapper(placeholderSql,argsMap);
    }
    public static <T> PlaceholderMapper buildPlaceholder(String placeholderSql, T entity){
        return new PlaceholderMapper(placeholderSql,entity);
    }

    @Override
    public void set(String fcName, Object value) {
        super.set(fcName, value);
        addCondition(SqlConst.AND,Condition.equal(fcName,value));
    }

    @Override
    public void clear() {
        super.clear();
        this.conditionMap.clear();
    }

    public void remove(SqlConst sqlConst) {
        conditionMap.remove(sqlConst);
    }

    @Override
    public List<Condition> getConditions(SqlConst sqlConst){
        return conditionMap.get(sqlConst);
    }

    @Override
    public int hashCode() {
        int result = obj.hashCode();
        for (SqlConst sqlConst : conditionMap.keySet()){
            List<Condition> list = conditionMap.get(sqlConst);
            if (!list.isEmpty()){
                for (Condition element : list){
                    result = 31 * result + (element == null ? 0 : element.hashCode());
                }
            }
        }
        return result;
    }

    private void fcNameWithOperator(SqlConst sqlConst,String fcNameWithOperator,Object...values){
        int i = fcNameWithOperator.indexOf(" ");
        Condition condition ;
        for (Object value : values){
            if (i==-1){
                condition = Condition.equal(fcNameWithOperator, value);
            }else{
                String fcName = fcNameWithOperator.substring(0,i).trim();
                String operator = fcNameWithOperator.substring(i).trim().toUpperCase();
                if (operator.equals("LIKE")){
                    condition = Condition.like(fcName,String.valueOf(value));
                }else{
                    condition = Condition.to(fcName, operator, value);
                }
            }
            addCondition(sqlConst,condition);
        }
    }

    private void addCondition(SqlConst key, Condition condition){
        addCondition(key,condition,false);
    }

    /**
     *
     * @param key
     * @param condition
     * @param isSingle 关键字是否只会用到一次
     */
    private void addCondition(SqlConst key, Condition condition, Boolean isSingle){
        if (condition.isEmpty()){
            return;
        }
        if (condition.getFcName()!=null){
            String fcName = condition.getFcName();
            FieldMapper mapper = classMapper.mapField(fcName);
            if (mapper == null){
                throw new NoSuchFieldOrColumnException(getObj(),fcName);
            }
            Condition.formatCondition(condition,mapper.getColumnName(),mapper.getColumnType());
        }
        List<Condition> list ;

        if(isSingle){
            conditionMap.put(key,Collections.singletonList(condition));
            return;
        }
        if (conditionMap.containsKey(key)){
            list = conditionMap.get(key);
        }else{
            list = new ArrayList();
            conditionMap.put(key,list);
        }
        list.add(condition);
    }

}
