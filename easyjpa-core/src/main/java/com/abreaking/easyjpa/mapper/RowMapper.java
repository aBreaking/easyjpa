package com.abreaking.easyjpa.mapper;


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 表的行 -> 对象 映射
 * @author liwei_paas
 * @date 2020/7/3
 */
public interface RowMapper<T> {

    T mapRow(ResultSet rs, int rowNum) throws SQLException;

}
