package com.abreaking.easyjpa.dao.condition;

import com.abreaking.easyjpa.sql.SqlBuilder;

/**
 * 将对象本身视作条件查询体。该接口可以将对象make成条件体（Matrix）以及拼接到Sql描述语句（sqlBuilder）
 * @author liwei_paas
 * @date 2020/11/26
 */
public interface Condition {

    /**
     * 接收一个SqlBuilder，将实体的生成可执行的sql
     * @param sqlBuilder
     */
    void accept(SqlBuilder sqlBuilder);


}
