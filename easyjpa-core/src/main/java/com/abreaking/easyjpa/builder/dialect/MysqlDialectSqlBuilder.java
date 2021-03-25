package com.abreaking.easyjpa.builder.dialect;

import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;

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
            columnMatrix.put("pageStartIndex",SqlUtil.getSqlType(Integer.class),pageStartIndex);
        }
        columnMatrix.put("pageSize",SqlUtil.getSqlType(Integer.class),pageSize);
    }
}
