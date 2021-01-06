package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.exception.EntityObjectNeedsException;
import com.abreaking.easyjpa.mapper.ClassMapper;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.mapper.MatrixMapper;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.mapper.matrix.MatrixFactory;
import com.abreaking.easyjpa.util.SqlUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 对实体的映射，接收一个实体对象，配合SqlBuilder 可将其处理成可执行的预sql
 * @author liwei_paas
 * @date 2020/12/3
 */
public abstract class EasyJpaMapper<T> implements MatrixMapper,RowMapper {

    // 实体的class对象
    protected Class obj;

    // 实体的映射信息
    protected ClassMapper classMapper;

    protected ColumnMatrix matrix;

    public EasyJpaMapper(Class<T> obj){
        this.obj = obj;
        this.classMapper = ClassMapper.map(obj);
        this.matrix = MatrixFactory.createColumnMatrix(0);
    }

    public EasyJpaMapper(T t){
        this.obj = t.getClass();
        this.classMapper = ClassMapper.map(this.obj);
        this.matrix = MatrixFactory.createColumnMatrix();
        for (FieldMapper fieldMapper : classMapper.allMappableFields()){
            try {
                Method getterMethod = fieldMapper.getGetterMethod();
                Object value = getterMethod.invoke(t);
                if (value!=null){
                    String columnName = fieldMapper.getColumnName();
                    int columnType = fieldMapper.getColumnType();
                    matrix.put(columnName,columnType,value);
                }
            } catch (IllegalAccessException|InvocationTargetException e) {
            }
        }
    }

    @Override
    public Matrix matrix() {
        return this.matrix;
    }

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 从结果集中映射出对象，如果是单值查询的话，考虑是否直接使用原对象)
        Object instance;
        try {
            instance = obj.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EntityObjectNeedsException(obj+"必须是实体的类，并且至少有一个空的构造方法");
        }
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            FieldMapper fieldMapper = classMapper.mapField(columnName);
            if (fieldMapper ==null){
                continue;
            }
            try {
                Field field = fieldMapper.getField();
                field.setAccessible(true);
                Object o = SqlUtil.getSoftResultSetValue(rs, i, field.getType());
                field.set(instance,o);
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return instance;
    }

    /**
     * 向matrix里继续set值
     * @param fcName
     * @param value
     */
    protected void set(String fcName,Object value){
        FieldMapper fieldMapper = classMapper.mapField(fcName);
        this.matrix.put(fieldMapper.getColumnName(),fieldMapper.getColumnType(),value);
    }

    protected void clear(){
        String[] columns = this.matrix.columns();
        for (int i = 0; i < columns.length; i++) {
            this.matrix.remove(i);
        }
    }

    public Matrix idMatrix(){
        ColumnMatrix columnMatrix = MatrixFactory.createColumnMatrix(1);
        String idName = this.getIdName();
        if (idName!=null){
            int i = matrix.indexOf(idName);
            if (i!=-1){
                columnMatrix.put(matrix.getColumn(i),matrix.getType(i),matrix.getValue(i));
                return columnMatrix;
            }
        }
        return null;
    }

    public String getTableName(){
        return classMapper.mapTableName();
    }

    public String getIdName(){
        FieldMapper idFieldMapper = classMapper.mapId();
        if (idFieldMapper != null){
            return idFieldMapper.getColumnName();
        }
        return null;
    }

    public Class getObj(){
        return this.obj;
    }

}
