package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import com.abreaking.easyjpa.executor.JdbcSqlExecutor;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MyDaoTest {


    public MyDaoTest() throws SQLException {
    }

    public  DataSource localhostDatasource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriver(new Driver());
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/test");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("mysqladmin");
        return druidDataSource;
    }

    public  DataSource ds2() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriver(new Driver());
        druidDataSource.setUrl("jdbc:mysql://172.18.231.40:8066/bcoc_test");
        druidDataSource.setUsername("bcoc_test");
        druidDataSource.setPassword("bcoc_test");
        return druidDataSource;
    }

    EasyJpaDao easyJpaDao = null;

    SqlExecutor sqlExecutor = new JdbcSqlExecutor(localhostDatasource().getConnection());

    EasyJpaDao dao = new EasyJpaDaoImpl(sqlExecutor);
    CurdTemplate curdTemplate = new CurdTemplate(sqlExecutor);

    @Test
    public void testSelect2(){
        EasyJpa easyJpa = new EasyJpa(User.class);
        List list = curdTemplate.select(easyJpa);
        prettyPrint(list);
    }

    @Test
    public void testUpdate(){
        EasyJpa<User> conditions = new EasyJpa<>(User.class);
        conditions.and(Condition.to("userId",">",10170));

        User user = new User();
        user.setBirthday(new Date());
        EasyJpa<User> easyJpa = new EasyJpa<>(user);
        prettyPrint(curdTemplate.select(conditions));

        curdTemplate.update(easyJpa,conditions);

        prettyPrint(curdTemplate.select(conditions));
    }

    @Test
    public void testDelete(){
        EasyJpa<User> jpa = new EasyJpa<>(User.class);
        jpa.and(Condition.to("userId",">=",10085));
        curdTemplate.delete(jpa);
    }

    @Test
    public void testInsert(){
        for (int i = 0; i < 100; i++) {
            User user = new User("a"+i,new Date());
            EasyJpa<User> easyJpa = new EasyJpa<>(user);
            curdTemplate.insert(easyJpa);
        }
    }

    @Test
    public void test04(){
        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.and(Condition.between("userId",10090,10112));
        easyJpa.or("userName like ","a1","a2");

        easyJpa.orderBy("userId",false);
        easyJpa.limit(0,100);
        List list = curdTemplate.select(easyJpa);
        prettyPrint(list);
    }

    @Test
    public void test03() throws SQLException {
        SqlExecutor sqlExecutor = new JdbcSqlExecutor(ds2().getConnection());
        EasyJpaDao dao = new EasyJpaDaoImpl(sqlExecutor);
        String sql = " SELECT m0.ability_menu_id,m0.ability_menu_name_cn,m1.ability_menu_id,m1.ability_menu_name_cn,m2.ability_menu_id,m2.ability_menu_name_cn from bcoc_ability_menu m0  LEFT JOIN bcoc_ability_menu m1 ON m1.ability_menu_pid = m0.ability_menu_id LEFT JOIN bcoc_ability_menu m2 ON m2.ability_menu_pid = m1.ability_menu_id";
        List<String[]> query = dao.queryByPrepareSql(sql, new Object[0], String.class);
        System.out.println(query);
    }


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
        if (list.isEmpty()){
            System.out.println("result is empty");
        }
        for (Object o : list){
            System.out.println(o);
        }
    }




}
