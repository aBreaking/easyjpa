package com.abreaking.easyjpa.sql.dialect;

import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;

/**
 * mysql的方言
 * @author liwei_paas
 * @date 2021/1/4
 */
public class MysqlDialectSqlBuilder extends AbstractDialectSqlBuilder{

    public MysqlDialectSqlBuilder(StringBuilder sqlBuilder) {
        super(sqlBuilder);
    }

    @Override
    public void visitPage(ColumnMatrix columnMatrix, int pageStartIndex, int pageSize) {
        if (pageStartIndex == 0){
            sqlBuilder.append("LIMIT ?");
            columnMatrix.put("pageSize",SqlUtil.getSqlType(Integer.class),pageSize);
        }else{
            sqlBuilder.append("LIMIT ?,?");
            columnMatrix.put("pageStartIndex",SqlUtil.getSqlType(Integer.class),pageStartIndex);
            columnMatrix.put("pageSize",SqlUtil.getSqlType(Integer.class),pageSize);
        }
    }
}
