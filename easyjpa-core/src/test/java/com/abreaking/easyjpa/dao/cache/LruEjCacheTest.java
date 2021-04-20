package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.JavaMapRowMapper;
import org.junit.Test;

/**
 * test for LruEjCache
 * @author liwei
 * @date 2021/3/5
 */
public class LruEjCacheTest {

    @Test
    public void test01(){
        LruEjCache cache = new LruEjCache(4,2);
        SelectKey c = new SelectKey(null,null);
        SelectKey c1 = new SelectKey(null,new JavaMapRowMapper());
        cache.hput("a1",c,1);
        cache.hput("a2",c,2);
        cache.hput("a3",c,3);
        cache.hput("a4",c,4);
        cache.hput("a5",c,5);
        cache.hput("a1",c1,11);
        cache.hput("a6",c,6);
        System.out.println(cache);
        System.out.println(cache.hget("a1",c));
        System.out.println(cache.hget("a1",c1));
    }

    @Test
    public void test02(){
        LruEjCache cache = new LruEjCache(4,8);

        for (int i = 0; i < 10; i++) {
            final String t = String.valueOf(i);
            new Thread(()->{
                //TODO 并发如何测试
            }).start();
        }

        SelectKey c1 = new SelectKey(null, null);
        SelectKey c2 = new SelectKey(new PreparedWrapper("1"), null);
        cache.hput("a1",c1,1);
        cache.hput("a1",c2,2);
        System.out.println(cache.hget("a1",c1));
        System.out.println(cache.hget("a1",c2));
    }
}
