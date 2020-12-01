package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.mapper.*;
import com.abreaking.easyjpa.mapper.exception.NoIdOrPkSpecifiedException;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.SqlBuilder;

import javax.xml.soap.Node;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 默认的Condition实现
 * @author liwei_paas
 * @date 2020/11/13
 */
public class EasyJpa<T> implements Condition{

    private Class obj;
    private ClassMatrixRowMapper classMatrixRowMapper;
    private List<Entry> entryList = new LinkedList<>();

    public EasyJpa(T t){
        this((Class<T>) t.getClass());
        initEntryList(t);
    }

    public EasyJpa(Class<T> obj){
        this.obj = obj;
        this.classMatrixRowMapper = ClassMatrixRowMapper.map(obj);
    }

    private void initEntryList(Object entity){
        ObjectMatrixMapper objectMatrixMapper = new ObjectMatrixMapper(entity);
        ColumnMatrix matrix = objectMatrixMapper.matrix();
        String[] columns = matrix.columns();
        for (int i = 0; i < columns.length; i++) {
            String c = columns[i];
            entryList.add(new Entry("set",c,"=",matrix.getValue(i)));
        }
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
        entryList.add(new Entry("set",filedName,operator,null));
    }

    private void add(String filedName,String operate,Object value){
        entryList.add(new Entry("add",filedName,operate,value));
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

    @Override
    public Matrix make(SqlBuilder sqlBuilder) {
        List<Entry> entryList = filter(this.entryList);
        ColumnMatrix condition = new AxisColumnMatrix();
        sqlBuilder.table(classMatrixRowMapper.tableName());
        for (Entry entry : entryList){
            condition.put(entry.columnName,entry.columnType,entry.value);
            sqlBuilder.add(entry.columnName,entry.operator);
        }
        return condition;
    }

    @Override
    public Matrix id() {
        if (!entryList.isEmpty()){
            ColumnMatrix condition = new AxisColumnMatrix(1);
            ColumnMatrix id = classMatrixRowMapper.mapId();
            if (id == null){
                throw new NoIdOrPkSpecifiedException(obj.getSimpleName()+"没有指定主键。请使用@Id注解来标识主键！");
            }
            String idColumn = id.getColumn(0);
            for (Entry entry :entryList){
                if (entry.columnName.equals(idColumn)){
                    condition.put(idColumn,entry.columnType,entry.value);
                    return condition;
                }
            }
        }
        return null;
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
