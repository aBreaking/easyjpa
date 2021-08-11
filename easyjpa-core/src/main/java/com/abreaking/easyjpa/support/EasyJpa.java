package com.abreaking.easyjpa.support;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.exception.NoSuchFieldOrColumnException;
import com.abreaking.easyjpa.mapper.ClassMapper;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.mapper.MatrixMapper;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 我们用easyJpa来完成单个对象（表） 增删改查的条件组装操作。
 * @author liwei
 * @date 2020/11/13
 */
public final class EasyJpa<T> implements Conditions,MatrixMapper {

    private final Map<SqlConst,List<Condition>> conditionMap ;

    private final ColumnMatrix columnMatrix;

    private final ClassMapper classMapper;

    private final Class obj;

    public EasyJpa(Class obj) {
        this.obj = obj;
        this.conditionMap = new HashMap<>(2);
        this.classMapper = ClassMapper.map(obj);
        this.columnMatrix = new AxisColumnMatrix();
    }

    public EasyJpa(T o) {
        this(o,null);
    }

    /**
     * 将对象直接视为查询条件
     * 在拼装条件之前，可自定义设置某个字段的条件操作符。如果不设置，那么默认条件操作符都为“=”
     * @param o
     * @param fieldOperatorMap
     */
    public EasyJpa(T o,Map<String,String> fieldOperatorMap) {
        this(o.getClass());
        List<Condition> list = new ArrayList<>();
        this.conditionMap.put(SqlConst.AND,list);
        for (FieldMapper fieldMapper : classMapper.allMappableFields()){
            try {
                Method getterMethod = fieldMapper.getGetterMethod();
                Object value = getterMethod.invoke(o);
                if (value!=null){
                    if (value instanceof String){ //空字符串也不应该考虑
                        String v = (String) value;
                        if (v.isEmpty()){
                            continue;
                        }
                    }
                    String columnName = fieldMapper.getColumnName();
                    int columnType = fieldMapper.getColumnType();
                    columnMatrix.put(columnName,columnType,value);
                    Condition condition = null;
                    if (fieldOperatorMap!=null){ //可以自行指定操作符号类型
                        String op = fieldOperatorMap.get(fieldMapper.getFiledName());
                        if (op==null){
                            op = fieldOperatorMap.get(columnName);
                        }
                        if (op!=null){
                            condition = Condition.to(columnName,op,value);
                        }
                    }
                    if (condition==null){
                        condition = Condition.equal(columnName,value);
                    }
                    condition.setSqlType(columnType);
                    list.add(condition);
                }
            } catch (IllegalAccessException|InvocationTargetException e) {
            }
        }
    }

    public void select(String...fcNames){
        String[] columnNames = new String[fcNames.length];
        for (int i = 0; i < fcNames.length; i++) {
            FieldMapper mapper = classMapper.mapField(fcNames[i]);
            columnNames[i] = mapper == null?fcNames[i]:mapper.getColumnName();
        }
        conditionMap.put(SqlConst.SELECT,Collections.singletonList(Condition.prepare("SELECT",columnNames)));
    }

    /**
     * and condition条件
     * @param condition
     */
    public void and(Condition condition){
        addCondition(SqlConst.AND,condition);
    }

    /**
     * 直接传入条件表达式
     * 比如：[col1 like ?,"%xxx%"]
     *
     * 先不考虑这种，因为：可能传入的直接就是一个整的条件表达式，这种考虑还需要处理列名嘛？
     *
     * @param conditionExpress
     * @param value
     */
    @Deprecated
    private void and(String conditionExpress,Object...value){
    }

    public void or(Condition condition){
        addCondition(SqlConst.OR,condition);
    }

    public void orderBy(String fieldNameOrColumnName){
        orderBy(fieldNameOrColumnName,true);
    }

    public void orderBy(String fieldNameOrColumnName,Boolean asc){
        addCondition(SqlConst.ORDER_BY,Condition.to(fieldNameOrColumnName, "ORDER BY", asc ? "ASC" : "DESC"));
    }

    public void groupBy(String...fieldNameOrColumnName){
        for (String fc : fieldNameOrColumnName){
            addCondition(SqlConst.GROUP_BY,Condition.to(fc, "GROUP BY",null));
        }
    }

    public void limit(int start,int offset){
        addCondition(SqlConst.LIMIT,Condition.prepare("limit ?,?",start,offset));
    }

    @Override
    public List<Condition> getConditions(SqlConst sqlConst){
        return conditionMap.get(sqlConst);
    }


    /**
     *
     * @param key
     * @param condition
     */
    private void addCondition(SqlConst key, Condition condition){
        formatColNameAndType(condition);
        List<Condition> list ;
        if (conditionMap.containsKey(key)){
            list = conditionMap.get(key);
        }else{
            list = new ArrayList();
            conditionMap.put(key,list);
        }
        list.add(condition);
    }

    /**
     * 对实体表的列名以及列类型的处理
     * 因为对列名进行一次处理，可能给定的列名并不是实例的列名，而是类中的字段名。
     * @param condition
     */
    private Condition formatColNameAndType(Condition condition){
        if (condition.getFcName()!=null){
            String fcName = condition.getFcName();
            FieldMapper mapper = classMapper.mapField(fcName);
            if (mapper == null){
                throw new NoSuchFieldOrColumnException(obj,fcName);
            }
            condition.setFcName(mapper.getColumnName());
            condition.setSqlType(mapper.getColumnType());
        }
        return condition;
    }

    @Override
    public Matrix matrix() {
        return columnMatrix;
    }

    public String getTableName() {
        return classMapper.mapTableName();
    }

    public Class getObj() {
        return obj;
    }

    public String getIdColumnName(){
        FieldMapper id = classMapper.mapId();
        if (id!=null){
            return id.getColumnName();
        }
        return null;
    }

    public Object getValue(String columnName){
        String[] columns = columnMatrix.columns();
        Object[] values = columnMatrix.values();
        for (int i = 0; i < columns.length; i++) {
            if (columnName.equals(columns[i])){
                return values[i];
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EasyJpa<?> easyJpa = (EasyJpa<?>) o;
        return Objects.equals(conditionMap, easyJpa.conditionMap) &&
                Objects.equals(columnMatrix, easyJpa.columnMatrix) &&
                Objects.equals(obj, easyJpa.obj);
    }

    @Override
    public int hashCode() {

        return Objects.hash(conditionMap, columnMatrix, obj);
    }
}
