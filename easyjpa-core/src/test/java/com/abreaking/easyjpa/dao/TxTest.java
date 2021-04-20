package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务的一些功能测试
 * @author liwei_paas
 * @date 2021/1/13
 */
public class TxTest {

    @Test
    public void test01() throws SQLException, InterruptedException {
        Connection connection = MyDataSource.localhostConnection();

        new Thread(()->{
            try {
                connection.setAutoCommit(true);
                EasyJpaDaoImpl dao = new EasyJpaDaoImpl(connection);

                User user = new User();
                user.setUserId(10086);
                user.setUserName("zhangsan");

                dao.insert(user);
                Thread.sleep(2000);

                connection.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(()->{
            try {
                EasyJpaDaoImpl dao = new EasyJpaDaoImpl(connection);
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(dao.get(User.class,10086));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }).start();

        Thread.sleep(100000);

    }

}
