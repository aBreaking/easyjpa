package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.executor.JdbcExecutor;
import com.abreaking.easyjpa.mapper.ObjectMatrixMapper;
import com.abreaking.easyjpa.mapper.ObjectRowMapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.sql.SelectSqlBuilder;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import org.junit.Test;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyDaoTest {

    final String jdbcurl = "jdbc:mysql://localhost:3306/test";
    final String username = "root";
    final String paasword = "mysqladmin";

    public DataSource druidDataSource() throws Exception {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriver(new Driver());
        druidDataSource.setUrl(jdbcurl);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(paasword);
        return druidDataSource;
    }

    private EasyJpa build(User user) throws Exception {
        EasyJpa easyJpa = EasyJpa.buildJdbcEasyJpa(user);
        easyJpa.setSqlExecutor(new JdbcExecutor(druidDataSource().getConnection()));
        return easyJpa;
    }

    @Test
    public void test01() throws Exception {
        User user = new User();
        user.setUserName("zhangsan");
        EasyJpa easyJpa = EasyJpa.buildJdbcEasyJpa(user);
        easyJpa.setSqlExecutor(new JdbcExecutor(druidDataSource().getConnection()));
        List query = easyJpa.query();
        System.out.println(query);
    }

    @Test
    public void test02() throws Exception {
        User user = new User();
        EasyJpa easyJpa = build(user);
        easyJpa.addLike("abc","%an%");
        //easyJpa.addGt("birthday",new SimpleDateFormat("yyyy-MM-dd").parse("2020-09-10"));

        System.out.println(easyJpa.query());
    }

}
