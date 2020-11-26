package com.abreaking.easyjpa.spring;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-config.xml"})
public class DemoTest {

    @Resource
    SpringJdbcTemplateExecutor springJdbcTemplateExecutor;

    @Resource
    EasyJpaDao springDao;

    @Test
    public void test01() throws SQLException {
        User user = new User();
        user.setUserId(1);
        EasyJpa easyJpa = springDao.build(user);
        List list = easyJpa.query();
        System.out.println(list);
    }
}
