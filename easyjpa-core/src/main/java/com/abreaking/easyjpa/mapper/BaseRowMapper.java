package com.abreaking.easyjpa.mapper;

import javax.swing.text.html.ObjectView;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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
    public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Map<String,Object> map = new HashMap<>(columnCount);

        for (int i = 1; i <= columnCount; i++) {
            Object value = getResultSetValue(rs, i, obj);
            String columnName = metaData.getColumnName(i);
            map.put(columnName,value);
        }
        return map;
    }

}
