package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.mapper.RowMapper;

import java.sql.SQLException;
import java.util.List;

/**
 * sql语句的执行器
 * 默认使用jdbc执行器，后续可以对接mybaits、spring的jdbcTemplate 等
 * @author liwei_paas
 * @date 2020/11/3
 */
public interface SqlExecutor {

    <T> List<T> query(String preparedSql, Object[] values, int[] types, RowMapper<T> rowMapper) throws SQLException;

    int update(String preparedSql, Object[] values, int[] types) throws SQLException;
}
