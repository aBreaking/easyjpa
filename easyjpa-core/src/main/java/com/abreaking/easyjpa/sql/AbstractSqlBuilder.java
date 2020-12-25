package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.BaseEasyJpa;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.mapper.matrix.MatrixFactory;


/**
 * 统一的sqlbuilder封装处理，使用StringBuilder来组装sql
 * @author liwei_paas
 * @date 2020/12/25
 */
public abstract class AbstractSqlBuilder implements SqlBuilder{

    StringBuilder sqlBuilder = new StringBuilder();

    protected abstract void doVisit(BaseEasyJpa easyJpa, ColumnMatrix columnMatrix);

    @Override
    public Matrix visit(BaseEasyJpa easyJpa) {
        ColumnMatrix columnMatrix = MatrixFactory.createColumnMatrix();
        doVisit(easyJpa,columnMatrix);
        return columnMatrix;
    }

    protected void cutLast(StringBuilder builder,String s){
        builder.replace(builder.lastIndexOf(s),builder.length(),"");
    }
}
