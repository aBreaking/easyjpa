package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.mapper.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

/**
 * @{USER}
 * @{DATE}
 */
public class EasyJpaDataSourceExecutor implements EasyJpaExecutor{

    DataSource dataSource;

    public EasyJpaDataSourceExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(Supplier<PreparedWrapper> sqlSupplier, RowMapper rowMapper) {
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            EasyJpaJdbcExecutor executor = new EasyJpaJdbcExecutor(connection);
            return executor.query(sqlSupplier,rowMapper);
        } catch (SQLException e) {
            throw new EasyJpaException("get connection from datasource failed!",e);
        } finally {
            if (connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new EasyJpaException(e);
                }
            }
        }
    }

    public void execute(Supplier<PreparedWrapper> sqlSupplier) {
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            EasyJpaJdbcExecutor executor = new EasyJpaJdbcExecutor(connection);
            executor.execute(sqlSupplier);
        } catch (SQLException e) {
            throw new EasyJpaException("get connection from datasource failed!",e);
        } finally {
            if (connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new EasyJpaException(e);
                }
            }
        }
    }
}
