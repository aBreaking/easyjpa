package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.executor.JdbcSqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;
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

    EasyJpaDao easyJpaDao = new EasyJpaDaoImpl(new JdbcSqlExecutor(druidDataSource().getConnection()));

    @Test
    public void test01() throws Exception {
        User user = new User();
        user.setUserId(1);
        /*EasyJpa<User> easyJpa = new EasyJpa<>(user);*/
        /*easyJpa.addGt("birthday",new SimpleDateFormat("yyyy-MM-dd").parse("2020-09-10"));*/


        List<User> list = easyJpaDao.select(user);
        System.out.println(list);
    }

    @Test
    public void test02(){
        EasyJpa easyJpa = new EasyJpa(User.class);
        List list = easyJpaDao.select(easyJpa);
        System.out.println(list);
    }

    @Test
    public void test03() throws InterruptedException {
        run(()->easyJpaDao.select(User.class));
        run(()->easyJpaDao.select(new User()));
        run(()->easyJpaDao.select(new EasyJpa(new User())));
        Thread.sleep(1000);
    }

    @Test
    public void test04(){
        User user = new User();
        user.setUserName("san");
        EasyJpa<User> userEasyJpa = new EasyJpa<>(user);
        userEasyJpa.setLike("userName");
        List list = easyJpaDao.select(userEasyJpa);
        System.out.println(list);
    }

    public static void run(Supplier supplier){
        new Thread(()->{
            System.out.println(Thread.currentThread().getName());
            Object o = supplier.get();
            System.out.println(o.hashCode());
            System.out.println(o);
        }).start();
    }
}
