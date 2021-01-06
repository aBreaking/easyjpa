package com.abreaking.easyjpa.mapper;


import java.sql.*;

/**
 * 结果集的映射
 * 表的行 -> 对象 映射
 * @author liwei_paas
 * @date 2020/7/3
 */
public interface RowMapper<T> {

    T mapRow(ResultSet rs, int rowNum) throws SQLException;

}
