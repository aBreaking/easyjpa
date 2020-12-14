package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.util.List;

/**
 * 统一的dao封装层,一键解决增删改查的问题
 * @author liwei_paas
 * @date 2020/11/26
 */
public interface EasyJpaDao<T> {

    List<T> select(Condition condition, RowMapper<T> rowMapper);

    int update(Condition set,Condition condition);

    int delete(Condition condition);

    int insert(Condition condition);
}
