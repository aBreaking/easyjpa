package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.mapper.*;
import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.SqlBuilder;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 默认的Condition实现
 * @author liwei_paas
 * @date 2020/11/13
 */
public class EasyJpa<T> implements Condition{

    // 实体的class对象
    private Class obj;
    // 实体的映射信息
    private ClassMatrixRowMapper classMatrixRowMapper;
    // 实体的属性描述
    private List<Entry> entityList = new LinkedList<>();

    public EasyJpa(T t){
        this((Class<T>) t.getClass());
        mapEntity(t);
    }

    public EasyJpa(Class<T> obj){
        this.obj = obj;
        this.classMatrixRowMapper = ClassMatrixRowMapper.map(obj);
    }

    public EasyJpa(Class<T> obj,Matrix matrix){
        this(obj);
        addEntityList(matrix);
    }

    /**
     * 实体对象的映射
     * @param entity
     */
    private void mapEntity(Object entity){
        List<Field> mappingFields = classMatrixRowMapper.getMappingFields();
        ColumnMatrix matrix = new AxisColumnMatrix();
        for (Field field : mappingFields){
            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value!=null){
                    String columnAndType = classMatrixRowMapper.getColumnAndType(field.getName());
                    String[] split = columnAndType.split(":");
                    matrix.put(split[0],Integer.parseInt(split[1]),value);
                }
            } catch (IllegalAccessException e) {
            }
        }
        addEntityList(matrix);
    }

    private void addEntityList(Matrix matrix){
        String[] columns = matrix.columns();
        Object[] values = matrix.values();
        for (int i = 0; i < columns.length; i++) {
            entityList.add(new Entry("set", columns[i], "=",values[i]));
        }
    }

    public void addValues(String filedName,Object value){
        add(filedName,"=",value);
    }

    public void addLike(String filedName,String value){
        add(filedName,"like",value);
    }

    public void setLike(String filedName){
        set(filedName,"like");
    }

    public void setGt(String filedName){
        set(filedName,">");
    }

    public void addGt(String filedName,Object value){
        add(filedName,">",value);
    }

    private void set(String filedName,String operator){
        entityList.add(new Entry("set",filedName,operator,null));
    }

    private void add(String filedName,String operate,Object value){
        entityList.add(new Entry("add",filedName,operate,value));
    }

    private List<Entry> filter(List<Entry> list){
        Map<String,Entry> map = new HashMap<>();
        for (Entry entry : list){
            String columnAndType = classMatrixRowMapper.getColumnAndType(entry.columnName);
            if (columnAndType == null){
                throw new IllegalArgumentException(obj.getSimpleName()+"类中不存在这样的字段名或列名："+entry.columnName);
            }
            String[] split = columnAndType.split(":");
            String column = split[0];
            int type = Integer.parseInt(split[1]);
            entry.columnName = column;
            entry.columnType = type;
            if (map.containsKey(column)){
                Entry filedEntry = map.get(column);
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

    public Field id(){
        return classMatrixRowMapper.getId();
    }

    @Override
    public Matrix make(SqlBuilder sqlBuilder) {
        List<Entry> entryList = filter(this.entityList);
        ColumnMatrix condition = new AxisColumnMatrix();
        sqlBuilder.table(classMatrixRowMapper.tableName());
        for (Entry entry : entryList){
            condition.put(entry.columnName,entry.columnType,entry.value);
            sqlBuilder.add(entry.columnName,entry.operator);
        }
        return condition;
    }

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        return classMatrixRowMapper.mapRow(rs,rowNum);
    }

    private static class Entry{
        private String type;
        private String columnName;
        private int columnType;
        private String operator;
        private Object value;

        Entry next;

        public Entry(String type,String columnName, String operator, Object value) {
            this.type = type;
            this.columnName = columnName;
            this.operator = operator;
            this.value = value;
        }

        @Override
        public boolean equals(Object entry) {
            if (entry instanceof Entry){
                Entry e  = (Entry) entry;
                return this.columnName.equals(e.columnName)&&this.operator.equals(e.operator);

            }else{
                return false;
            }
        }

        public void addNext(Entry entry){
            //先去重处理
            if (this.equals(entry)){
                this.value = entry.value;
                return;
            }

            if (this.next == null){
                this.next = entry;
                return;
            }
            this.next.addNext(entry);
        }
    }
}
