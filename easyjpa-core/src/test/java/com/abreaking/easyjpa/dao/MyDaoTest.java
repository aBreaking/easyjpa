package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import com.abreaking.easyjpa.executor.JdbcSqlExecutor;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class MyDaoTest {


    public MyDaoTest() throws SQLException {
    }

    public  static DataSource localhostDatasource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriver(new Driver());
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf-8");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("mysqladmin");
        return druidDataSource;
    }

    public static DataSource ds2() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriver(new Driver());
        druidDataSource.setUrl("jdbc:mysql://172.18.231.40:8066/bcoc_test");
        druidDataSource.setUsername("bcoc_test");
        druidDataSource.setPassword("bcoc_test");
        return druidDataSource;
    }

    public static EasyJpaDao easyJpaDao = null;

    public static SqlExecutor sqlExecutor;

    static {
        try {
            sqlExecutor = new JdbcSqlExecutor(localhostDatasource().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static  EasyJpaDao dao = new EasyJpaDaoImpl(sqlExecutor);
    public static CurdTemplate curdTemplate = new CurdTemplate(sqlExecutor);



    private void prettyPrint(List list){
        if (list.isEmpty()){
            System.out.println("result is empty");
        }
        for (Object o : list){
            System.out.println(o);
        }
    }




}
