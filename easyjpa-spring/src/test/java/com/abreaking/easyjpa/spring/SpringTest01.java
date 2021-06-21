package com.abreaking.easyjpa.spring;

import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.support.EasyJpa;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @{USER}
 * @{DATE}
 */

public class SpringTest01 {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");

        EasyJpaDao dao = context.getBean(EasyJpaDao.class);
        System.out.println(dao.queryByPage(new EasyJpa(User.class),new Page(1,2)).getResult());
        System.out.println(dao.queryByPage(new EasyJpa(User.class),new Page(2,2)).getResult());



        context.start();
        System.in.read();


    }
}
