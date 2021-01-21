package com.abreaking.easyjpa.util;

import com.abreaking.easyjpa.exception.ErrorTypeException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

/**
 * sql工具
 * 一些方法是已 “getSoft” 开头这种，会对没有获取到值或异常 再进行一层处理
 * @author liwei_paas
 * @date 2019/11/22
 */
public class SqlUtil {

    /**
     * 根据java的字段类型转sql的字段类型
     * @param fieldType
     * @return
     */
    public static int getSoftSqlType(Class<?> fieldType){
        int fullSqlType = getSqlType(fieldType);
        if (fullSqlType == Types.NULL){
            fullSqlType =  Types.VARCHAR;
        }
        return fullSqlType;
    }

    public static int getSqlType(Class<?> fieldType){
        if(String.class.isAssignableFrom(fieldType))return Types.VARCHAR;
        if(Integer.class.isAssignableFrom(fieldType))return Types.INTEGER;
        if(Long.class.isAssignableFrom(fieldType))return Types.NUMERIC;
        if(Float.class.isAssignableFrom(fieldType))return Types.FLOAT;
        if(Double.class.isAssignableFrom(fieldType))return Types.DOUBLE;
        if(Date.class.isAssignableFrom(fieldType))return Types.TIMESTAMP;
        return Types.NULL;
    }

    public static Class getSoftJavaType(int columnType){
        switch (columnType){
            case Types.VARCHAR : return String.class;
            case Types.INTEGER : return Integer.class;
            case Types.SMALLINT : return Integer.class;
            case Types.TINYINT : return Integer.class;
            case Types.BIGINT : return Long.class;
            case Types.FLOAT : return Float.class;
            case Types.DOUBLE : return Double.class;
            case Types.NUMERIC : return Double.class;
            case Types.TIMESTAMP : return Date.class;
            default: return String.class;
        }
    }


    /**
     * 根据指定类型，获取返回结果集的类型
     * @param rs
     * @param index
     * @param requiredType
     * @return
     * @throws SQLException
     */
    public static Object getSoftResultSetValue(ResultSet rs, int index, Class<?> requiredType) {
        try {
            return getResultSetValue(rs,index,requiredType);
        } catch (SQLException e) {
            throw new ErrorTypeException("The result type specified in column "+index
                    +" is incorrect. The data of the column cannot be converted to type of "+requiredType.getName()+". " +
                    "(If you are not sure about the type of the column, try the String type)",e);
        }
    }

    public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        if (String.class == requiredType) return rs.getString(index);
        if (boolean.class == requiredType || Boolean.class == requiredType) return rs.getBoolean(index);
        if (byte.class == requiredType || Byte.class == requiredType) return rs.getByte(index);
        if (short.class == requiredType || Short.class == requiredType) return rs.getShort(index);
        if (int.class == requiredType || Integer.class == requiredType) return rs.getInt(index);
        if (long.class == requiredType || Long.class == requiredType) return rs.getLong(index);
        if (float.class == requiredType || Float.class == requiredType) return rs.getFloat(index);
        if (double.class == requiredType || Double.class == requiredType ||Number.class == requiredType) return rs.getDouble(index);
        if (BigDecimal.class == requiredType) return rs.getBigDecimal(index);
        if (java.sql.Date.class == requiredType) return rs.getDate(index);
        if (Time.class == requiredType) return rs.getTime(index);
        if (Timestamp.class == requiredType || java.util.Date.class == requiredType) return rs.getTimestamp(index);
        if (byte[].class == requiredType) return rs.getBytes(index);
        if (Blob.class == requiredType) return rs.getBlob(index);
        if (Clob.class == requiredType) return rs.getClob(index);
        if (Object.class == requiredType)return rs.getObject(index);
        return rs.getString(index);
    }
}
