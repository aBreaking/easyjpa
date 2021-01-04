package com.abreaking.easyjpa.sql.dialect;

import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;

/**
 * 不同类型的数据库方言会不一样
 * 需要在各个子类实际去对方言进行处理，比如分页
 * @author liwei_paas
 * @date 2021/1/4
 */
public abstract class AbstractDialectSqlBuilder {
    StringBuilder sqlBuilder;

    public AbstractDialectSqlBuilder(StringBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    /**
     * 分页
     * @param columnMatrix
     * @param pageStartIndex 分页开始的行号（从O开始）
     * @param pageSize 分页大小
     */
    public abstract void visitPage(ColumnMatrix columnMatrix, int pageStartIndex, int pageSize);

}
