package com.abreaking.easyjpa.builder;


import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;

/**
 * sql语句的构造器
 * 它构造的一般都是预处理的sql语句：形如： query * from table where column = ?
 * @author liwei_paas
 * @date 2020/11/3
 */
public interface SqlBuilder {

    PreparedWrapper visit(Conditions conditions);
}
