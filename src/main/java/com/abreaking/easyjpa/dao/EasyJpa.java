package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.ClassMatrixMapper;
import com.abreaking.easyjpa.mapper.MatrixMapper;
import com.abreaking.easyjpa.mapper.ObjectMatrixMapper;
import com.abreaking.easyjpa.mapper.ObjectRowMapper;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.IndexColumnMatrix;
import com.abreaking.easyjpa.sql.SelectSqlBuilder;
import com.abreaking.easyjpa.sql.SqlBuilder;

import java.sql.SQLException;
import java.util.*;

/**
 * 默认curld的统一入口方法
 * @author liwei_paas
 * @date 2020/11/13
 */
public class EasyJpa<T> {

    T t;
    MatrixMapper mapper;
    SqlExecutor sqlExecutor;

    private List<Entry> entryList = new LinkedList<>();

    private EasyJpa(T t){
        this.t = t;
        this.mapper = new ObjectMatrixMapper(t);
        this.initEntryList();
    }

    private void initEntryList(){
        ColumnMatrix matrix = mapper.matrix();
        String[] columns = matrix.columns();
        for (int i = 0; i < columns.length; i++) {
            String c = columns[i];
            entryList.add(new Entry(c,"=",matrix.getValue(i)));
        }
    }

    public static <T> EasyJpa buildJdbcEasyJpa(T t){
        return new EasyJpa(t);
    }

    protected void setSqlExecutor(SqlExecutor executor){
        this.sqlExecutor = executor;
    }

    public List<T> query() throws SQLException {
        SqlBuilder sqlBuilder = new SelectSqlBuilder(mapper.tableName());
        ColumnMatrix condition = condition(sqlBuilder);
        String prepareSql = sqlBuilder.toSql();
        System.out.println(prepareSql);
        System.out.println(Arrays.toString(condition.values()));
        return sqlExecutor.queryForList(prepareSql, condition.values(), condition.types(), new ObjectRowMapper(t.getClass()));
    }

    public void addLike(String filedName,String value){
        entry(filedName,"like",value);
    }

    public void setLike(String filedName){
        entry(filedName,"like");
    }

    public void setGt(String filedName){
        entry(filedName,">");
    }
    public void addGt(String filedName,Object value){
        entry(filedName,">",value);
    }

    private void entry(String filedName,String operate,Object value){
        entryList.add(new Entry(filedName,operate,value));
    }

    private void entry(String filedName,String operate){
        entryList.add(new Entry(filedName,operate,null));
    }

    private ColumnMatrix condition(SqlBuilder sqlBuilder){
        Class<?> obj = t.getClass();
        //通过hashMap去重
        Map<String,Entry> map = new HashMap<>();
        for (Entry entry : entryList){
            String key = entry.fieldName+"_"+entry.operator;
            if (map.containsKey(key) && entry.value==null){
                //如果当前没有value，说明是set，使用之前的value
                entry.value = map.get(key).value;
            }
            map.put(key,entry);
        }
        ClassMatrixMapper classMatrixMapper = ClassMatrixMapper.map(obj);
        ColumnMatrix condition = new AxisColumnMatrix();
        for (Entry entry : map.values()){
            String fieldName = entry.fieldName;
            String columnType = classMatrixMapper.getColumnAndType(fieldName);
            if (columnType==null){
                throw new IllegalArgumentException(obj.getSimpleName()+"类中不存在这样的字段名或列名："+fieldName);
            }
            String[] split = columnType.split(":");
            String column = split[0];
            int type = Integer.parseInt(split[1]);
            condition.put(column,type,entry.value);
            sqlBuilder.add(column,entry.operator);
        }
        return condition;
    }

    private static class Entry{
        private String fieldName;
        private String operator;
        private Object value;

        public Entry(String fieldName, String operate, Object value) {
            this.fieldName = fieldName;
            this.operator = operate;
            this.value = value;
        }
    }



}
