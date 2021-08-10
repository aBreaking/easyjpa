package com.abreaking.easyjpa.dao.condition;

import java.util.List;

/**
 * sql条件的封装。
 * 它的作用是根据指定的sql关键字，获取所有的condition。 最后交由SqlBuilder来组装成sql语句
 * @see Condition
 * @see SqlConst
 * @see com.abreaking.easyjpa.builder.SqlBuilder
 * @author liwei_paas
 * @date 2020/12/29
 */
public interface Conditions {

    /**
     * 指定sql关键字 的所有封装后的条件
     * @param sqlConst
     * @return
     */
    List<Condition> getConditions(SqlConst sqlConst);
}
