package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author liwei_paas
 * @date 2020/12/30
 */
public class ReadmeTest {

    public ReadmeTest() throws SQLException {
    }

static Connection localhostConnection()  {
    String jdbcDriver = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://localhost:3306/test?characterEncoding=utf-8";
    String jdbcUserName = "root";
    String jdbcPassword = "mysqladmin";
    try {
        Class.forName(jdbcDriver);
        return DriverManager.getConnection(jdbcUrl,jdbcUserName,jdbcPassword);
    }catch (Exception e){
        throw new RuntimeException(e);
    }
}

static EasyJpaDao dao = new EasyJpaDaoImpl(localhostConnection());

    @Test
    public void test05() throws ParseException {
        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.or(Condition.like("userName","a"));
        easyJpa.or(Condition.like("userName","z"));
        easyJpa.and(Condition.to("birthday",">",new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-10")));
        easyJpa.and(Condition.between("user_id",2,6));
        easyJpa.limit(1,10);
        List<User> userList = dao.query(easyJpa);
        prettyPrint(userList);
    }

    @Test
    public void test03(){
        User user = new User();
        user.setUserId(1);
        user.setUserName("lisi");
        dao.update(user);
    }

    @Test
     public void test04(){
        User user = dao.get(User.class, 1);
        System.out.println(user);
    }

    @Test
    public void test02(){
        dao.delete(User.class,1);
    }

    @Test
    public void test01(){
        User user = new User();
        user.setUserName("zhangsan");
        user.setBirthday(new Date());
        dao.insert(user);
    }

    private void prettyPrint(List list){
        if (list.isEmpty()){
            System.out.println("result is empty");
        }
        for (Object o : list){
            System.out.println(o);
        }
    }
}
