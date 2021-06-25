package com.abreaking.easyjpa.builder;

import com.abreaking.easyjpa.User;
import com.abreaking.easyjpa.builder.prepare.PlaceholderWrapper;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.ReadmeTest;
import com.abreaking.easyjpa.mapper.JavaMapRowMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liwei_paas
 * @date 2021/1/7
 */
public class PlaceHolderSqlBuilderTest {

    static Map<String,Object> map = new HashMap<>();
    static {
        map.put("user","user");
        map.put("userName","%name%");
        map.put("height",1.7);
    }

    /**
     * query的安全测试
     */
    @Test
    public void test_safe(){
        Map<String,Object> argMap = new HashMap<>();
        String sql = "query * from user where user_id=${userId}";
        argMap.put("userId","3; delete from user where user_id=3");
        PlaceholderWrapper mapper = new PlaceholderWrapper(sql, argMap);
        EasyJpaDao dao = ReadmeTest.dao;
        List list = dao.query(mapper, new JavaMapRowMapper()); //执行失败
        System.out.println(list);

    }

    @Test
    public void test01(){
        EasyJpaDao dao = ReadmeTest.dao;

        PlaceholderWrapper placeholderWrapper = new PlaceholderWrapper();
        placeholderWrapper.setArgMap(map);
        placeholderWrapper.addArgByClass(User.class);

        placeholderWrapper.append("query * from ${User} where 1=1 ");
        placeholderWrapper.appendIfArgValueNotNull(" and user_name like #{userName}","userName");
        placeholderWrapper.appendIfArgValueNotNull(" and height > #{height}","height");


        List<User> list = dao.query(placeholderWrapper, new JavaMapRowMapper());
        System.out.println(list);
    }


}
