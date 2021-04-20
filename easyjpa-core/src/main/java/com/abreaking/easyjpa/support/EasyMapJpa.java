package com.abreaking.easyjpa.support;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.mapper.MatrixMapper;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.SqlUtils;
import com.abreaking.easyjpa.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author liwei
 * @date 2021/3/12
 */
public class EasyMapJpa implements MatrixMapper,Conditions {

    String tableName;

    ColumnMatrix matrix;

    public EasyMapJpa(Map<String, Object> map){
        this(map,false);
    }

    public EasyMapJpa(Map<String, Object> map,Boolean formatKey2Column) {
        this.matrix = new AxisColumnMatrix(map.size());
        map.forEach((k,v)->matrix.put(formatKey2Column?StringUtils.underscoreName(k):k,SqlUtils.getSqlType(v.getClass()),v));
    }

    @Override
    public Matrix matrix() {
        return matrix;
    }

    @Override
    public List<Condition> getConditions(SqlConst sqlConst) {
        return null;
    }
}
