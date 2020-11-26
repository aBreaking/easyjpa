package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.SqlBuilder;

/**
 *
 * @author liwei_paas
 * @date 2020/11/26
 */
public interface Condition extends RowMapper {

    Matrix make(SqlBuilder sqlBuilder);
}
