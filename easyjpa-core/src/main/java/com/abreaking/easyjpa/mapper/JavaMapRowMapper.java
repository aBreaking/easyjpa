package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.util.SqlUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 将结果集 映射成java 的map对象
 * @author liwei_paas
 * @date 2020/12/15
 */
public class JavaMapRowMapper implements RowMapper{

    @Override
    public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Map<String,Object> map = new HashMap<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            int columnType = metaData.getColumnType(i);
            String columnName = metaData.getColumnName(i);
            Object value = getResultSetValue(rs, i, SqlUtil.getJavaType(columnType));
            map.put(columnName,value);
        }
        return map;
    }

}
