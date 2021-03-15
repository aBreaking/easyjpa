package com.abreaking.easyjpa.support;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.mapper.RowMapper;

/**
 *
 * @author liwei
 * @date 2021/3/15
 */
public interface Selectable {

    String table();

    Conditions conditions();

    RowMapper rowMap();

}
