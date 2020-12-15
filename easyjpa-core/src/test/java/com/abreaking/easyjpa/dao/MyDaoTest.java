package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import com.abreaking.easyjpa.executor.JdbcSqlExecutor;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class MyDaoTest {

    final String jdbcurl = "jdbc:mysql://localhost:3306/test";
    final String username = "root";
    final String paasword = "mysqladmin";

    public MyDaoTest() throws SQLException {
    }

    public  DataSource druidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriver(new Driver());
        druidDataSource.setUrl(jdbcurl);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(paasword);
        return druidDataSource;
    }

    EasyJpaDao easyJpaDao = null;

    SqlExecutor sqlExecutor = new JdbcSqlExecutor(druidDataSource().getConnection());

    EasyJpaDao dao = new EasyJpaDaoImpl(sqlExecutor);

    @Test
    public void test01(){
        EasyJpa easyJpa = new EasyJpa(new User());
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(2);
        page.setOrderBy("user_id desc");
        dao.queryByPage(easyJpa,page);
        System.out.println(page);
        prettyPrint(page.getResult());
    }

    private void prettyPrint(List list){
        for (Object o : list){
            System.out.println(o);
        }
    }




}
