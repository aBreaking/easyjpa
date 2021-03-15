package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.util.SqlUtil;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 将结果集 映射成java 的map对象
 * @author liwei_paas
 * @date 2020/12/15
 */
public class JavaMapRowMapper implements RowMapper{

    private Class[] returnTypes;

    public JavaMapRowMapper(Class[] returnTypes) {
        this.returnTypes = returnTypes;
    }

    public JavaMapRowMapper() {
    }

    @Override
    public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Map<String,Object> map = new HashMap<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            int columnType = metaData.getColumnType(i);
            String columnName = metaData.getColumnName(i);
            Class type = returnTypes!=null&&returnTypes.length>=i?returnTypes[i-1]:SqlUtil.getSoftJavaType(columnType);
            Object value = SqlUtil.getSoftResultSetValue(rs, i, type);
            map.put(columnName,value);
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaMapRowMapper rowMapper = (JavaMapRowMapper) o;
        return Arrays.equals(returnTypes, rowMapper.returnTypes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(returnTypes);
    }
}
