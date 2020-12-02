package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.exception.EntityObjectNeedsException;
import com.abreaking.easyjpa.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;

/**
 * 对结果集的映射
 * @author liwei_paas
 * @date 2020/11/3
 */
public class ClassRowMapper implements RowMapper{

    /**
     * 如果obj!=null,每次的结果集映射都会实例化一个对象出来
     */
    protected Class obj;

    public ClassRowMapper(Class obj){
        this.obj = obj;
    }
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 从结果集中映射出对象，如果是单值查询的话，考虑是否直接使用原对象)
        Object instance = null;
        try {
            instance = obj.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EntityObjectNeedsException(obj+"必须是实体的类，并且至少有一个空的构造方法");
        }

        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            String fieldName = StringUtils.deunderscoreName(columnName);
            try {
                Field field = obj.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object o = getResultSetValue(rs, i, field.getType());
                field.set(instance,o);
            } catch (Exception e) {
                continue;
            }
        }
        return instance;
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
}
