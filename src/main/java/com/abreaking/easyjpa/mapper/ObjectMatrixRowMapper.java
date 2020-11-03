package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.exception.EntityObjectNeedsException;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * 实体对象的映射关系描述
 * @author liwei_paas
 * @date 2020/7/13
 */
public class ObjectMatrixRowMapper implements MatrixMapper,RowMapper {

    Object entity;

    ClassMatrixMapper classMatrixMapper;

    public ObjectMatrixRowMapper(Class obj){
        this.classMatrixMapper = ClassMatrixMapper.map(obj);
        try {
            this.entity = obj.newInstance();
        } catch (InstantiationException |IllegalAccessException e) {
            throw new EntityObjectNeedsException(obj+"必须是实体的类，并且至少有一个空的构造方法");
        }
    }

    public ObjectMatrixRowMapper(Object entity){
        this.classMatrixMapper = ClassMatrixMapper.map(entity.getClass());
        this.entity = entity;
    }

    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Class obj = entity.getClass();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            String fieldName = StringUtils.deunderscoreName(columnName);
            try {
                Field field = obj.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object o = getResultSetValue(rs, i, field.getType());
                field.set(entity,o);
            } catch (Exception e) {
                continue;
            }
        }
        return entity;
    }

    @Override
    public String tableName() {
        return classMatrixMapper.tableName();
    }

    @Override
    public ColumnMatrix mapId() {
        return matrixFields(Collections.singletonList(classMatrixMapper.getId()), classMatrixMapper.mapId());
    }

    @Override
    public ColumnMatrix mapPks() {
        return matrixFields(classMatrixMapper.getPks(), classMatrixMapper.mapPks());
    }

    @Override
    public ColumnMatrix matrix() {
        return matrixFields(classMatrixMapper.getMappingFields(), classMatrixMapper.matrix());
    }

    public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        if (String.class == requiredType) {
            return rs.getString(index);
        }
        else if (boolean.class == requiredType || Boolean.class == requiredType) {
            return rs.getBoolean(index);
        }
        else if (byte.class == requiredType || Byte.class == requiredType) {
            return rs.getByte(index);
        }
        else if (short.class == requiredType || Short.class == requiredType) {
            return rs.getShort(index);
        }
        else if (int.class == requiredType || Integer.class == requiredType) {
            return rs.getInt(index);
        }
        else if (long.class == requiredType || Long.class == requiredType) {
            return rs.getLong(index);
        }
        else if (float.class == requiredType || Float.class == requiredType) {
            return rs.getFloat(index);
        }
        else if (double.class == requiredType || Double.class == requiredType ||
                Number.class == requiredType) {
            return rs.getDouble(index);
        }
        else if (BigDecimal.class == requiredType) {
            return rs.getBigDecimal(index);
        }
        else if (Date.class == requiredType) {
            return rs.getDate(index);
        }
        else if (Time.class == requiredType) {
            return rs.getTime(index);
        }
        else if (Timestamp.class == requiredType || java.util.Date.class == requiredType) {
            return rs.getTimestamp(index);
        }
        else if (byte[].class == requiredType) {
            return rs.getBytes(index);
        }
        else if (Blob.class == requiredType) {
            return rs.getBlob(index);
        }
        else if (Clob.class == requiredType) {
            return rs.getClob(index);
        }
        else  {
            throw new RuntimeException("error type");
        }
    }

    private ColumnMatrix matrixFields(List<Field> fields,ColumnMatrix matrix){
        String[] columns = matrix.columns();
        ColumnMatrix newMatrix = new AxisColumnMatrix(columns.length);
        for (int i = 0; i < columns.length; i++) {
            Field field = fields.get(i);
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if (value!=null){
                    newMatrix.put(matrix.getColumn(i),matrix.getType(i),value);
                }
            } catch (IllegalAccessException e) {
                //这里异常可以忽略，因为前面已经判断了，这些字段都是从实体类中有getter方法的字段
            }
        }
        return newMatrix;
    }
}
