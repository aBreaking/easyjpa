package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Entry;
import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;
import com.abreaking.easyjpa.mapper.ClassMapper;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.SqlBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author liwei_paas
 * @date 2020/12/3
 */
public abstract class AbstractEasyJpa<T> implements Condition {

    // 实体的class对象
    private Class obj;

    // 实体的映射信息
    protected final ClassMapper classMapper;
    // 实体的属性描述
    protected final Map<Entry,String> entryMap = new LinkedHashMap<>();

    public AbstractEasyJpa(T t){
        this((Class<T>) t.getClass());
        mapEntity(t);
    }

    public AbstractEasyJpa(Class<T> obj){
        this.obj = obj;
        this.classMapper = ClassMapper.map(obj);
    }

    public AbstractEasyJpa(Class<T> obj,Matrix matrix){
        this(obj);
        addEntityList(matrix);
    }

    public void accept(SqlBuilder sqlBuilder){
        sqlBuilder.visit(this);
    }

    /**
     * 将实体对象的属性进行映射
     * @param entity
     */
    private void mapEntity(Object entity){
        Map<String, FieldMapper> fieldsMapper = classMapper.getFieldsMapper();
        ColumnMatrix matrix = new AxisColumnMatrix();
        for (FieldMapper fieldMapper : fieldsMapper.values()){
            try {
                Method getterMethod = fieldMapper.getGetterMethod();
                Object value = getterMethod.invoke(entity);
                if (value!=null){ // 值不为空的对象将其放入matrix中去
                    matrix.put(fieldMapper.getColumnName(), fieldMapper.getColumnType(),value);
                }
            } catch (IllegalAccessException|InvocationTargetException e) {
            }
        }
        addEntityList(matrix);
    }

    /**
     * matrix to entityList
     * @param matrix
     */
    private void addEntityList(Matrix matrix){
        String[] columns = matrix.columns();
        Object[] values = matrix.values();
        for (int i = 0; i < columns.length; i++) {
            doAddEntry("set", columns[i], "=",values[i]);
        }
    }

    public String getTableName(){
        return classMapper.getTableName();
    }

    public String getIdName(){
        FieldMapper idFieldMapper = classMapper.getIdFieldMapper();
        if (idFieldMapper == null){
            throw new NoIdOrPkSpecifiedException(obj.getName()+"has no primary key! STRONGLY RECOMMEND: every table should has primary key,You can use the @Id or @Pk annotation to identify the primary key on your entity class");
        }
        return idFieldMapper.getColumnName();
    }

    public Entry getIdEntry(){
        String idName = getIdName();
        Set<Entry> entries = entryMap.keySet();
        for (Entry entry : entries){
            if (idName.equals(entry.getColumnName())){
                return entry;
            }
        }
        return null;
    }

    public Collection<Entry> entry(){
        return this.entryMap.keySet();
    }

    public Class getObj(){
        return this.obj;
    }

    /**
     * 直接set entityList(一般是实体对象)里某个字段 设置操作符
     * @param fcName 字段名 或 列名
     * @param operator 操作符
     */
    protected void set(String fcName,String operator){
        doAddEntry("set",fcName,operator,null);
    }

    /**
     * 向entityList(一般是实体对象)添加字段、操作符
     * @param fcName 字段名 或 列名
     * @param operator 操作符
     * @param value
     */
    protected void add(String fcName,String operator,Object value){
        doAddEntry("add",fcName,operator,value);
    }

    private void doAddEntry(String type,String fcName,String operator,Object value){
        FieldMapper fieldMapper = classMapper.getMapper(fcName);
        if (fieldMapper == null){
            throw new IllegalArgumentException(obj.getSimpleName()+"类中不存在这样的字段名或列名："+fcName);
        }
        Entry entry = new Entry();
        entry.setColumnName(fieldMapper.getColumnName());
        entry.setOperator(operator);
        entry.setValue(value);
        entryMap.put(entry,type);
    }

    /**
     * 组装entry
     * 先去重(根据columnName 及operator判断)
     * 将相同columnName的entry 用链表形式组装
     * @author liwei_paas
     * @date 2020/12/3
     */
    /*private List<Entry> filter(List<Entry> list){
        Map<String,Entry> map = new HashMap<>();
        for (Entry entry : list){
            if (map.containsKey(entry.columnName)){
                Entry filedEntry = map.get(entry.columnName);
                // 如果add 有了 = 这种操作符呢？
                if (entry.type.equals("add")){
                    filedEntry.addNext(entry);
                }else if (entry.type.equals("set")){
                    filedEntry.operator = entry.operator;
                    entry = filedEntry;
                }
            }else{
                map.put(entry.columnName,entry);
            }
            // like 的处理
            if (entry.operator.equals("like")){
                String value = (String) entry.value;
                String likeValue = value.indexOf("%")==-1 && value.indexOf("_")==-1 ? "%"+value+"%" : value;
                entry.value = likeValue;
            }
        }
        Collection<Entry> values = map.values();
        List<Entry> ret = new ArrayList<>();
        for (Entry entry :values){
            ret.add(entry);
            Entry e = entry.next;
            while (e!=null){
                ret.add(e);
                e = e.next;
            }
        }
        return ret;
    }


    private static class Node{
        private String type;
        private Entry entry;
        Node next;

        @Override
        public boolean equals(Object entry) {
            if (entry instanceof Entry){
                Entry e  = (Entry) entry;
                return this.columnName.equals(e.columnName)&&this.operator.equals(e.operator);

            }else{
                return false;
            }
        }

        public void addNext(Node node){
            //先去重处理
            if (this.equals(node)){
                this.value = entry.value;
                return;
            }

            if (this.next == null){
                this.next = entry;
                return;
            }
            this.next.addNext(entry);
        }
    }*/
}
