package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.mapper.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * 数据的sql执行器
 * @author liwei_paas
 * @date 2020/11/3
 */
public class JdbcDsSqlExecutor implements SqlExecutor{

    private DataSource dataSource;

    public JdbcDsSqlExecutor(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new EasyJpaException(e);
        }
    }

    @Override
    public <T> List<T> query(String preparedSql, Object[] values, int[] types, RowMapper<T> rowMapper) throws SQLException {
        JdbcSqlExecutor jdbcSqlExecutor = new JdbcSqlExecutor(dataSource.getConnection());
        return jdbcSqlExecutor.query(preparedSql,values,types,rowMapper);
    }

    @Override
    public int execute(String preparedSql, Object[] values, int[] types) throws SQLException {
        JdbcSqlExecutor jdbcSqlExecutor = new JdbcSqlExecutor(dataSource.getConnection());
        return jdbcSqlExecutor.execute(preparedSql,values,types);
    }

}
