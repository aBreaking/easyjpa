package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.exception.NoSuchFieldOrColumnException;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.sql.SqlConst;

/**
 * 对单个对象的增删改查 条件的默认是实现
 *
 * @author liwei_paas
 * @date 2020/11/13
 */
public final class EasyJpa<T> extends BaseEasyJpa {

    public EasyJpa(T o) {
        super(o);
    }

    public EasyJpa(Class obj) {
        super(obj);
    }

    public void selelct(String...fcNames){
        String[] columnNames = new String[fcNames.length];
        for (int i = 0; i < fcNames.length; i++) {
            FieldMapper mapper = classMapper.getMapper(fcNames[i]);
            if (mapper == null){
                throw new NoSuchFieldOrColumnException(getObj(),fcNames[i]);
            }
            columnNames[i] = mapper.getColumnName();
        }
        addCondition(SqlConst.SELECT,Condition.to("SELECT",columnNames));
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
        addCondition(SqlConst.LIMIT,Condition.to("limit ?,?",start,page));
    }


    private void fcNameWithOperator(SqlConst sqlConst,String fcNameWithOperator,Object...values){
        int i = fcNameWithOperator.indexOf(" ");
        Condition condition ;
        for (Object value : values){
            if (i==-1){
                condition = Condition.equal(fcNameWithOperator, value);
            }else{
                String fcName = fcNameWithOperator.substring(0,i).trim();
                String operator = fcNameWithOperator.substring(i).trim();
                condition = Condition.to(fcName, operator, value);
            }
            addCondition(sqlConst,condition);
        }
    }
}
