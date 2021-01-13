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
    /**
     * sql的数据库连接
     * 从connection可以获取数据库的相关信息，进而可以初始化一些全局配置
     * @return Connection
     */
    ConnectionHolder getConnectionHolder();

    /**
     * 通用的查询执行器，它主要是执行一条select 语句，然后将结果用rowMapper映射后返回
     * @param preparedSql
     * @param values
     * @param types
     * @param rowMapper
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T> List<T> query(String preparedSql, Object[] values, int[] types, RowMapper<T> rowMapper) throws SQLException;

    /**
     * 执行一条任意一条sql语句，一般是数据修改相关的操作
     * 可以是DML也可以是DDL 语句
     * @param preparedSql
     * @param values
     * @param types
     * @return 修改的记录数
     * @throws SQLException
     */
    int execute(String preparedSql, Object[] values, int[] types) throws SQLException;
}
