package com.abreaking.easyjpa.spring;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-config.xml"})
public class DemoTest {
    @Resource
    EasyJpaDao easyJpaDao;

    @Test
    public void test01(){
        EasyJpa easyJpa = new EasyJpa(new User());
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(2);
        page.setOrderBy("user_id asc");
        easyJpaDao.queryByPage(easyJpa,page);
        System.out.println(page);
        System.out.println(page.getResult());
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setUserId(10);
        user.setUserName("abc");
        user.setBirthday(new Date());
        easyJpaDao.insert(user);

        Object o = easyJpaDao.get(User.class, 10);
        System.out.println(o);
    }

    @Test
    public void testUpdate(){
        User user = new User();
        user.setUserId(10);
        user.setUserName("liwei");
        easyJpaDao.update(user);
        System.out.println(easyJpaDao.get(User.class,10));
    }

    @Test
    public void testDelete(){
        System.out.println(easyJpaDao.get(User.class,10));
    }
}
