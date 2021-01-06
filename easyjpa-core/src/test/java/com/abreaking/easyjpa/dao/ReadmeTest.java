package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.dao.prepare.PreparedMapper;
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
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
            connection.setAutoCommit(true); // update/insert/delete 自动提交
            return connection;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static EasyJpaDao dao = new EasyJpaDaoImpl(localhostConnection());


    @Test
    public void test09(){
    }

    @Test
    public void testPage(){
        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.select("userName");
        Page page = new Page();
        page.setPageSize(3);

        page.setPageNum(1);
        dao.queryByPage(easyJpa, page);
        System.out.println(page);
        prettyPrint(page.getResult());

        page.setPageNum(2);
        dao.queryByPage(easyJpa, page);
        System.out.println(page);
        prettyPrint(page.getResult());

        page.setPageNum(3);
        dao.queryByPage(easyJpa, page);
        System.out.println(page);
        prettyPrint(page.getResult());

    }

    @Test
    public void test08(){
        String prepareSql = "select user_name,height from ${tableName} where user_name like #{userName} and height>#{height}";
        User user = new User();
        user.setUserName("%王%");
        user.setHeight(1.7F);
        PlaceholderMapper placeholderMapper = EasyJpa.buildPlaceholder(prepareSql, user);
        List<User> list = dao.queryByPlaceholderSql(placeholderMapper,User.class);
        prettyPrint(list);
    }

    @Test
    public void test07(){
        String prepareSql = "select user_id,user_name from user where user_name like ? and height>?";
        PreparedMapper preparedMapper = EasyJpa.buildPrepared(prepareSql, "%王%", 1.7F);
        List<User> list = dao.queryByPreparedSql(preparedMapper,User.class);
        prettyPrint(list);
    }

    @Test
    public void test06() throws ParseException {
        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.select("userName","birthday");
        easyJpa.or(Condition.like("userName","张"));
        easyJpa.or(Condition.like("userName","李"));
        easyJpa.and(Condition.to("birthday",">",new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-10")));
        easyJpa.and(Condition.between("user_id",0,8));
        easyJpa.orderBy("userName",false);
        easyJpa.limit(0,5);
        List<User> userList = dao.queryByCondition(easyJpa);
        prettyPrint(userList);
    }

    @Test
    public void test05(){
        User user = new User();
        user.setUserName("zhangsan");
        user.setUserId(1);
        List<User> userList = dao.query(user);
        prettyPrint(userList);
    }

    @Test
    public void test04(){
        User user = dao.get(User.class, 1);
        System.out.println(user);
    }

    @Test
    public void test031(){
        User user = new User();
        user.setBirthday(new Date());

        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.and(Condition.like("userName","张"));

        dao.updateByCondition(user,easyJpa);
    }


    @Test
    public void test03(){
        User user = new User();
        user.setUserId(1); // 指定主键值
        user.setUserName("张大三"); // 只修改userName这个字段
        dao.update(user);
    }



    @Test
    public void test02(){
        dao.deleteById(User.class,1);
    }

    @Test
    public void test01(){
        User user = new User();
        user.setUserId(1);
        user.setUserName("张三");
        user.setBirthday(new Date());
        user.setHeight(1.81F);
        user.setPhoneNo(1008611L);
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
