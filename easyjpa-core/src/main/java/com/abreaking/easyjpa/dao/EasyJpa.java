package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.exception.NoSuchFieldOrColumnException;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.dao.condition.SqlConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fixme 说些什么
 *
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
            if (mapper == null){
                columnNames[i] = fcNames[i];
                //throw new NoSuchFieldOrColumnException(getObj(),fcNames[i]);
            }else{
                columnNames[i] = mapper.getColumnName();
            }
        }
        addCondition(SqlConst.SELECT,Condition.prepare("SELECT",columnNames));
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

    public void limit(int start,int page){
        addCondition(SqlConst.LIMIT,Condition.prepare("limit ?,?",start,page));
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
        if (conditionMap.containsKey(key)){
            list = this.conditionMap.get(key);
        }else{
            list = new ArrayList<>();
            conditionMap.put(key,list);
        }
        list.add(condition);
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

    @Override
    public List<Condition> getConditions(SqlConst sqlConst){
        return conditionMap.get(sqlConst);
    }
}
