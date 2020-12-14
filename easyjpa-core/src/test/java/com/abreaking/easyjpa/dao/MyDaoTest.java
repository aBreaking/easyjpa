package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.executor.JdbcSqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

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

    CurdTemplate<User> userCurdTemplate = new CurdTemplate<>(new JdbcSqlExecutor(druidDataSource().getConnection()));

    @Test
    public void test02() throws Exception {
        System.out.println("select-----------");

        System.out.println(userCurdTemplate.select(new User()));

        System.out.println("insert-----------");
        User user = new User();
        user.setUserId(10084);
        user.setUserName("abc");
        user.setBirthday(new Date());
        userCurdTemplate.insert(user);
        System.out.println(userCurdTemplate.select(new User()));
    }

    public void test04(){

    }


    //TODO
    @Test
    public void testUpdate(){
        User cuser = new User();
        cuser.setUserId(1);

        User user = new User();
        user.setUserName("abreaking");
        user.setBirthday(new Date());

        int update = easyJpaDao.update(new EasyJpa<>(user),new EasyJpa(cuser));
        System.out.println(update);
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setUserName("liwei");
        user.setUserId(2012);
        user.setBirthday(new Date());
        int insert = easyJpaDao.insert(new EasyJpa<>(user));
        System.out.println(insert);
    }

    @Test
    public void testDelete(){
        User user = new User();
        user.setUserId(2012);
        int insert = easyJpaDao.delete(new EasyJpa<>(user));
        System.out.println(insert);
    }




}
