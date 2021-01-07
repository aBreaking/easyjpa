package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.MyDaoTest;
import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.dao.prepare.PreparedMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import org.junit.Test;

import java.util.Arrays;
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
        map.put("userId",2);
        map.put("userName","李");
        map.put("height",1.7);
    }


    @Test
    public void test02(){
        PlaceholderMapper preparedMapper = EasyJpa.buildPlaceholder("select * from ${user} where 1=1 and user_id=#{userId} and user_name like concat('%',#{userName},'%')", map);
        List list = MyDaoTest.dao.queryByPlaceholderSql(preparedMapper);
        MyDaoTest.prettyPrint(list);
    }

    @Test
    public void test01(){
        PlaceholderMapper placeholderMapper = new PlaceholderMapper();
        placeholderMapper.addSqlFragment("select * from ${user} where 1=1");
        placeholderMapper.addSqlFragment("and user_id>#{abc}");
        placeholderMapper.addSqlFragment("and height >#{height}");

        placeholderMapper.setArgsMap(map);

        List list = MyDaoTest.dao.queryByPlaceholderSql(placeholderMapper);
        MyDaoTest.prettyPrint(list);

    }

    private void print(PlaceholderMapper placeholderMapper){
        PlaceHolderSqlBuilder sqlBuilder = new PlaceHolderSqlBuilder(placeholderMapper);
        Matrix matrix = sqlBuilder.visit(null);
        System.out.println(sqlBuilder.toString());
        System.out.println(Arrays.toString(matrix.values()));
    }
}
