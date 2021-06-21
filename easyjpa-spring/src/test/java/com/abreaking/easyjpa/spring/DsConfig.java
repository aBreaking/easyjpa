package com.abreaking.easyjpa.spring;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @{USER}
 * @{DATE}
 */
@Configuration
public class DsConfig {

    @Bean
    public static DataSource localDataSource()  {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String jdbcUrl = "jdbc:mysql://localhost:3306/test?characterEncoding=utf-8";
        String jdbcUserName = "root";
        String jdbcPassword = "mysqladmin";
        try {
            Class.forName(jdbcDriver);
            DruidDataSource ds = new DruidDataSource();
            ds.setDriverClassName(jdbcDriver);
            ds.setUrl(jdbcUrl);
            ds.setUsername(jdbcUserName);
            ds.setPassword(jdbcPassword);
            return ds;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
