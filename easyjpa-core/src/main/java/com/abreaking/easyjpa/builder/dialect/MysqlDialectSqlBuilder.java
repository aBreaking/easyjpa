package com.abreaking.easyjpa.builder.dialect;

import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtils;

/**
 * mysql的方言
 * @author liwei_paas
 * @date 2021/1/4
 */
public class MysqlDialectSqlBuilder extends DialectSqlBuilder{


    @Override
    public void visitPage(StringBuilder sqlBuilder,ColumnMatrix columnMatrix, int pageStartIndex, int pageSize) {
        sqlBuilder.append(" LIMIT ");
        if (pageStartIndex == 0){
            sqlBuilder.append("?");
        }else{
            sqlBuilder.append("?,?");
            columnMatrix.put("pageStartIndex",SqlUtils.getSqlType(Integer.class),pageStartIndex);
        }
        columnMatrix.put("pageSize",SqlUtils.getSqlType(Integer.class),pageSize);
    }
}
