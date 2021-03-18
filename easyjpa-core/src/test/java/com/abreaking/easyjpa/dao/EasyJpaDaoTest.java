package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import com.abreaking.easyjpa.support.EasyJpa;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;


/**
 * EasyJpaDao 测试用例
 * @author liwei
 * @date 2021/3/16
 */
public class EasyJpaDaoTest {

    public static EasyJpaDao dao = new EasyJpaDaoImpl(MyDataSource.localhostConnection());

    @Test
    public void test_insert(){
        User user = new User();
        user.setUserName("lisi");
        user.setBirthday(new Date());
        user.setHeight(1.9F);
        dao.insert(user);
        System.out.println(dao.query(user));
    }

    @Test
    public void test_deleteByCondition(){
        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.and(Condition.like("userName","张%"));
        dao.deleteByCondition(easyJpa);
    }

    @Test
    public void test_deleteById(){
        dao.deleteById(User.class,1);
        System.out.println(dao.get(User.class,1));
    }

    @Test
    public void test_updateByCondition(){
        User user = new User();
        user.setBirthday(new Date());
        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.and(Condition.like("userName","张%"));
        dao.updateByCondition(user,easyJpa);
        System.out.println(dao.queryByCondition(easyJpa));
    }

    @Test
    public void test_update(){
        User user = new User();
        user.setUserId(10);
        System.out.println(dao.get(User.class,10));
        user.setHeight(1.66F);
        dao.update(user);
        System.out.println(dao.get(User.class,10));

    }

    @Test
    public void test_update_noId(){
        User user = new User();
        user.setHeight(1.66F);
        dao.update(user); // 抛出 .EasyJpaException: the object of class com.abreaking.easyjpa.User must have id value
    }

    @Test
    public void test_queryByPage(){
        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.and(Condition.to("height","<",2));
        easyJpa.orderBy("height");
        Page page = new Page(2,2);
        dao.queryByPage(easyJpa,page);
        System.out.println(page); //分页的数据
        System.out.println(page.getResult()); //第2页，三行数据
    }

    @Test
    public void test_queryByCondition(){
        EasyJpa easyJpa = new EasyJpa(User.class);
        System.out.println(dao.queryByCondition(easyJpa)); //all

        easyJpa.and(Condition.like("userName","三"));
        System.out.println(dao.queryByCondition(easyJpa)); //张大三 , 张三

        easyJpa.and(Condition.between("height",1.1F,2F));
        System.out.println(dao.queryByCondition(easyJpa)); //张三
    }

    @Test
    public void test_query(){
        User user = new User();
        System.out.println("all->"+dao.query(user)); // all

        user.setHeight(2F);
        System.out.println(dao.query(user)); // userid = 2 ,3

        user.setUserId(2);
        System.out.println(dao.query(user)); // userId = 2
    }

    @Test
    public void test_get(){
        User user = dao.get(User.class, 1);
        System.out.println(user);
        Assert.assertEquals("张大三",user.getUserName());

        User userWillBeNull = dao.get(User.class, 99999);
        System.out.println(userWillBeNull);
        Assert.assertNull(userWillBeNull);
    }


}


