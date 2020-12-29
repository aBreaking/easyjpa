package com.abreaking.easyjpa.sql;


import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.Matrix;

/**
 * sql语句的构造器
 * 它构造的一般都是预处理的sql语句：形如： select * from table where column = ?
 * @author liwei_paas
 * @date 2020/11/3
 */
public interface SqlBuilder {

    Matrix visit(EasyJpa easyJpa);
}
