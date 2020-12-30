package com.abreaking.easyjpa.util;

import java.sql.Types;
import java.util.Date;

/**
 * sql工具
 * @author liwei_paas
 * @date 2019/11/22
 */
public class SqlUtil {


    /**
     * 根据java的字段类型转sql的字段类型
     * @param fieldType
     * @return
     */
    public static int getSqlType(Class<?> fieldType){
        //字段类型处理，这里处理三种：String,Number,Date
        return String.class.isAssignableFrom(fieldType)?Types.VARCHAR:
                Integer.class.isAssignableFrom(fieldType)?Types.INTEGER:
                Long.class.isAssignableFrom(fieldType)?Types.NUMERIC:
                Float.class.isAssignableFrom(fieldType)?Types.FLOAT:
                Double.class.isAssignableFrom(fieldType)?Types.DOUBLE:
                Date.class.isAssignableFrom(fieldType)?Types.TIMESTAMP:
                Types.VARCHAR;
    }

    public static Class getJavaType(int columnType){
        switch (columnType){
            case Types.VARCHAR : return String.class;
            case Types.INTEGER : return Integer.class;
            case Types.NUMERIC : return Long.class;
            case Types.FLOAT : return Float.class;
            case Types.DOUBLE : return Double.class;
            case Types.TIMESTAMP : return Date.class;
            default: return String.class;
        }
    }
}
