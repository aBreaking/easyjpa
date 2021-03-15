package com.abreaking.easyjpa.dao.prepare;

import com.abreaking.easyjpa.mapper.matrix.Matrix;

/**
 * 预处理的sql及其参数封装
 * @author liwei
 * @date 2021/2/23
 */
public class PreparedWrapper {

    String preparedSql; //预处理的sql语句

    Matrix matrix;  //sql里的参数

    public PreparedWrapper(String preparedSql, Matrix matrix) {
        this.preparedSql = preparedSql;
        this.matrix = matrix;
    }

    public String getPreparedSql() {
        return preparedSql;
    }

    public void setPreparedSql(String preparedSql) {
        this.preparedSql = preparedSql;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
}
