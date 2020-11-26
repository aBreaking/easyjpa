package com.abreaking.easyjpa.spring;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 一个数据源配置
 * @author liwei_paas
 * @date 2020/7/14
 */
@Configuration
public class DataSourceConfig {

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

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(ds);
    }

}
