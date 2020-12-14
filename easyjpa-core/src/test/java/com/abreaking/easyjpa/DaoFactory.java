package com.abreaking.easyjpa;

import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 *
 * @author liwei_paas
 * @date 2020/12/8
 */
public class DaoFactory {

    final static String jdbcurl = "jdbc:mysql://localhost:3306/test";
    final static String username = "root";
    final static String paasword = "mysqladmin";

    public static DataSource druidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriver(new Driver());
        druidDataSource.setUrl(jdbcurl);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(paasword);
        return druidDataSource;
    }


    public static EasyJpaDao getDao() throws SQLException {
        return null;
    }
}
