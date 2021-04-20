package com.abreaking.easyjpa.builder;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.support.EasyJpa;
import com.abreaking.easyjpa.dao.condition.Condition;
import org.junit.Test;

/**
 *
 * @author liwei_paas
 * @date 2021/2/20
 */
public class SelectSqlBuilderTest {


    @Test
    public void test01(){
        SelectSqlBuilder selectSqlBuilder = new SelectSqlBuilder("user");

        EasyJpa easyJpa = new EasyJpa(User.class);
        easyJpa.and(Condition.like("userName","张"));
        easyJpa.or(Condition.like("userName","王"));
        easyJpa.or(Condition.like("userName","李"));
        easyJpa.orderBy("userId",false);
        easyJpa.limit(1,2);
    }
}
