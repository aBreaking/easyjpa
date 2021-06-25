package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import com.abreaking.easyjpa.mapper.JavaMapRowMapper;
import com.abreaking.easyjpa.support.EasyJpa;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * EasyJpaDao 测试用例
 * @author liwei
 * @date 2021/3/16
 */
public class EasyJpaDaoTest {

    public static EasyJpaDao dao = new EasyJpaDaoImpl(MyDataSource.localhostConnection());

    @Test
    public void test_anything(){
        List<Map> list = dao.query(new PreparedWrapper("query count(*) counter from user where 1=2"), new JavaMapRowMapper());
        Long count = (Long) list.get(0).get("counter");
        System.out.println(count);
    }

    @Test
    public void test_curdTemplateSelect(){
    }

    @Test
    public void test_batchInsert(){
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User("name" + i, new Date());
            user.setHeight(1.7f+0.01f*i);
            user.setPhoneNo(123450L+i);
            list.add(user);
        }
        dao.insertBatch(list);
    }

    @Test
    public void test_thread() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            int s = i;
            new Thread(()->{
                User user = dao.get(User.class, s%9);
                System.out.println(Thread.currentThread().getName()+"->"+s%9+"->"+user);
                if (user!=null && user.getUserId()!=s%9){
                    System.err.println(user);
                }
            }).start();
        }
        Thread.currentThread().join();

    }

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
        //easyJpa.and(Condition.to("height","<",2));
        //easyJpa.orderBy("height");
        Page page = new Page(1,2);
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

        easyJpa = new EasyJpa(User.class);
        easyJpa.and(Condition.between("height",1.8F,2F));
        System.out.println(dao.queryByCondition(easyJpa)); //张三
    }

    @Test
    public void test_query(){
        User user = new User();
        System.out.println("all->"+dao.query(user)); // all


        user.setUserId(4);
        System.out.println(dao.query(user));
    }

    @Test
    public void test_get(){
        User user = dao.get(User.class, 10088);
        System.out.println(user);
        Assert.assertEquals("name0",user.getUserName());

        User userWillBeNull = dao.get(User.class, 99999);
        System.out.println(userWillBeNull);
        Assert.assertNull(userWillBeNull);
    }


}


