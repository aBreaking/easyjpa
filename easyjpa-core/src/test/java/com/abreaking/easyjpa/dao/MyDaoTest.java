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
        EasyJpa<User> userEasyJpa = new EasyJpa<>(user);
        List<User> list = easyJpaDao.select(userEasyJpa);
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
        run(()->easyJpaDao.select(new EasyJpa(new User())));
        Thread.sleep(1000);
    }

    @Test
    public void test04(){
        User user = new User();
        user.setUserName("zhangsan");
        EasyJpa<User> userEasyJpa = new EasyJpa<>(user);
        userEasyJpa.addLike("user_name","lisi");
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

    @Test
    public void test05() throws ParseException {
        EasyJpa<User> userEasyJpa = new EasyJpa<>(User.class);
        userEasyJpa.addLike("userName","an");
        userEasyJpa.addGt("birthday",new SimpleDateFormat("yyyy-MM-dd").parse("2020-09-06"));

        List list = easyJpaDao.select(userEasyJpa);
        System.out.println(list);
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
