package com.abreaking.easyjpa.support;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.mapper.MatrixMapper;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liwei
 * @date 2021/3/12
 */
public class EasyMapJpa implements EasyExecutable {

    Map<String,Object> map = new HashMap<>();

    public EasyMapJpa(Map<String, Object> map) {
        this.map = map;
    }

    public List<Condition> getConditions(SqlConst sqlConst) {
        if (sqlConst.equals(SqlConst.AND)){
            map.forEach((k,v)->{

            });
        }
        return null;
    }

    @Override
    public Selectable select() {
        return new Selectable() {
            @Override
            public String table() {
                return null;
            }

            @Override
            public Conditions conditions() {
                return null;
            }

            @Override
            public RowMapper rowMap() {
                return null;
            }
        };
    }
}
