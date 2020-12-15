package com.abreaking.easyjpa.spring;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.crypto.Data;

/**
 * 一个数据源配置
 * @author liwei_paas
 * @date 2020/7/14
 */
@Configuration
public class DataSourceConfigTest {

    static String JDBC_URl = "jdbc:mysql://localhost:3306/test";
    static String JDBC_USERNAME = "root";
    static String JDBC_PASSWORD = "mysqladmin";
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static DruidDataSource ds;

    static {
        ds = new DruidDataSource();
        ds.setUrl(JDBC_URl);
        ds.setUsername(JDBC_USERNAME);
        ds.setPassword(JDBC_PASSWORD);
        ds.setDriverClassName(JDBC_DRIVER);
    }

    @Bean
    public DataSource dataSource(){
        return ds;
    }


}
