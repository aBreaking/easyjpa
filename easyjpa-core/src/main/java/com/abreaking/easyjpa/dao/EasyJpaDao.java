package com.abreaking.easyjpa.dao;

import java.util.List;

/**
 * 统一的dao封装层,一键解决增删改查的问题
 * @author liwei_paas
 * @date 2020/11/26
 */
public interface EasyJpaDao<T> {

    List<T> select(Condition condition);
    List<T> select(T t);

    int update(Condition condition);
    int update(T t);

    int delete(Condition condition);
    int delete(T t);

    int insert(Condition condition);
    int insert(T t);
}
