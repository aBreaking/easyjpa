package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.support.EasyJpa;
import com.abreaking.easyjpa.dao.prepare.PreparedMapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;

import java.sql.Types;


/**
 *
 * prepareSql的默认实现
 * 它支持直接传入可执行的sql
 * @author liwei_paas
 * @date 2020/12/29
 */
public class PrepareSqlBuilder extends AbstractSqlBuilder{

    private PreparedMapper preparedMapper;


    public PrepareSqlBuilder(PreparedMapper preparedMapper){
        this.preparedMapper = preparedMapper;
    }

    @Override
    protected void doVisit(EasyJpa easyJpa, ColumnMatrix columnMatrix) {
        sqlBuilder.append(preparedMapper.getPrepareSql());
        if (preparedMapper.getArgs()!=null){
            int i = 0;
            for (Object value : preparedMapper.getArgs()){
                int type = value==null?Types.NULL : SqlUtil.getSoftSqlType(value.getClass());
                columnMatrix.put(String.valueOf(i),type,value);
            }
        }

    }

}
