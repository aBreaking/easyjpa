package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.AbstractEasyJpa;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.SqlUtil;

/**
 * 
 * @author liwei_paas 
 * @date 2020/12/13
 */
public abstract class MatrixSqlBuilder implements SqlBuilder {
    
    protected ColumnMatrix columnMatrix = new AxisColumnMatrix();
    protected StringBuilder sqlBuilder = new StringBuilder();
    
    private int i = 0;
    
    public MatrixSqlBuilder(){
    }

    protected abstract void doVisit(AbstractEasyJpa easyJpa);
    
    protected MatrixSqlBuilder add(String sql,Object...values){
        sqlBuilder.append(sql);
        if (sql.indexOf("?")!=-1){
            for (Object value : values){
                columnMatrix.put("prefix_"+i++,SqlUtil.getSqlType(value.getClass()),value);
            }
            return this;
        }
        if (sql.indexOf("${")!=-1){
            // FIXME
        }
        if (sql.indexOf("#{")!=-1){
            // FIXME
        }

        for (Object value :values){
            sqlBuilder.append(" ").append(value);
        }
        return this;
    }

    @Override
    public void visit(AbstractEasyJpa condition) {
        this.doVisit(condition);
    }

    @Override
    public String toPrepareSql(){
        return sqlBuilder.toString();
    }

    @Override
    public Matrix toMatrix() {
        return this.columnMatrix;
    }
}
