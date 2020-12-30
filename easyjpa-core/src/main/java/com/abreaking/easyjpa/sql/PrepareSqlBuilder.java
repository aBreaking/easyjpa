package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;


/**
 *
 * prepareSql的默认实现
 * 它支持直接传入可执行的sql
 * @author liwei_paas
 * @date 2020/12/29
 */
public class PrepareSqlBuilder extends AbstractSqlBuilder{


    String prepareSql;

    Object[] values ;


    public PrepareSqlBuilder(String prepareSql,Object...values){
        this.prepareSql = prepareSql;
        this.values = values;
    }

    @Override
    protected void doVisit(EasyJpa easyJpa, ColumnMatrix columnMatrix) {
        sqlBuilder.append(prepareSql);
        int i = 0;
        for (Object value : values){
            columnMatrix.put("placeholder"+i,SqlUtil.getSqlType(value.getClass()),value);
        }
    }

}
