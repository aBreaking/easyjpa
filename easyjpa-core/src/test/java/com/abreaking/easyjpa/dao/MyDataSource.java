package com.abreaking.easyjpa.dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 测试的数据源
 * @author liwei
 * @date 2021/3/16
 */
public class MyDataSource {

    public static Connection localhostConnection()  {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String jdbcUrl = "jdbc:mysql://localhost:3306/test?characterEncoding=utf-8";
        String jdbcUserName = "root";
        String jdbcPassword = "mysqladmin";
        try {
            Class.forName(jdbcDriver);
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
            connection.setAutoCommit(true); // update/insert/delete 自动提交
            return connection;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Connection bjOracleConnection()  {
        String jdbcDriver = "oracle.jdbc.OracleDriver";
        String jdbcUrl = "jdbc:oracle:thin:@//172.18.238.234:1524/tthradb";
        String jdbcUserName = "esbdb";
        String jdbcPassword = "Paas#123";
        try {
            Class.forName(jdbcDriver);
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
            connection.setAutoCommit(true); // update/insert/delete 自动提交
            return connection;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
