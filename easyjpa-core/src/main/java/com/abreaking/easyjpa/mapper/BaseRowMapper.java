package com.abreaking.easyjpa.mapper;

import java.math.BigDecimal;
import java.sql.*;
/**
 * 将结果集映射成数组
 * @author liwei_paas
 * @date 2020/12/15
 */
public class BaseRowMapper implements RowMapper{
    
    Class obj;

    public BaseRowMapper(Class obj) {
        this.obj = obj;
    }

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Object[] ret = new Object[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            ret[i-1] = getResultSetValue(rs, i, obj);
        }
        return ret;
    }

    protected static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
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
        }else if (Object.class == requiredType){
            return rs.getObject(index);
        }else  {
            throw new RuntimeException("error type");
        }
    }
}
