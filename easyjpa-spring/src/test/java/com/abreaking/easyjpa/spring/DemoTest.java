package com.abreaking.easyjpa.spring;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-config.xml"})
public class DemoTest {

    @Resource
    EasyJpaDao dao;

    @Test
    public void test01() {
        User user = new User();
        user.setUserId(1);
        List<User> list = dao.select(new EasyJpa(user));
        System.out.println(list);
    }

    @Test
    public void test02(){
        User user = new User();
        user.setUserId(1);
        int delete = dao.delete(new EasyJpa(user));
        List list = dao.select(new EasyJpa(user));
        System.out.println(list);
        user.setBirthday(new Date());
        user.setUserName("zhoujielun");
        dao.insert(new EasyJpa(user));
        User user1 = new User();user1.setUserId(1);
        System.out.println(dao.select(new EasyJpa<>(user1)));


    }
}
