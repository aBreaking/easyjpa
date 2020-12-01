package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.SqlBuilder;

/**
 * 将对象本身视作条件查询体。该接口可以将对象make成条件体（Matrix）以及拼接到Sql描述语句（sqlBuilder）
 * @author liwei_paas
 * @date 2020/11/26
 */
public interface Condition extends RowMapper {

    Matrix make(SqlBuilder sqlBuilder);

    Matrix id();
}
