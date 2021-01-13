package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.exception.EasyJpaException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 包装jdbc的connection
 * @author liwei_paas
 * @date 2021/1/13
 */
public class ConnectionHolder {

    private Connection connection;

    private DatabaseMetaData metaData;

    private String jdbcUrl ;
    private String jdbcUserName ;
    private String jdbcDriverName ;


    public ConnectionHolder(Connection connection) {
        this.connection = connection;
        try{
            this.metaData = connection.getMetaData();
            this.jdbcUrl = metaData.getURL();
            this.jdbcUserName = metaData.getUserName();
            this.jdbcDriverName = metaData.getDriverName();
        }catch (SQLException e){
            throw new EasyJpaException(e);
        }

    }

    public Connection getConnection() {
        return connection;
    }

    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUserName() {
        return jdbcUserName;
    }

    public String getJdbcDriverName() {
        return jdbcDriverName;
    }
}
