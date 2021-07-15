package com.abreaking.easyjpa.builder.dialect;


import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtils;

/**
 * oracle的方言
 * @author liwei_paas
 * @date 2021/1/4
 */
public class OracleDialectSqlBuilder extends DialectSqlBuilder{

    @Override
    public void visitPage(StringBuilder sqlBuilder,ColumnMatrix columnMatrix, int pageStartIndex, int pageSize) {
        int rowStart = pageStartIndex+1;
        int rowEnd = pageSize+pageStartIndex;
        sqlBuilder.insert(0,"SELECT * FROM (SELECT ej_tmp.*, ROWNUM ej_rowStart FROM ( ");
        sqlBuilder.append(" ) ej_tmp WHERE ROWNUM <= ?) WHERE ej_rowStart >= ?");
        columnMatrix.put("rowEnd",SqlUtils.getSqlType(Integer.class),rowEnd);
        columnMatrix.put("rowStart",SqlUtils.getSqlType(Integer.class),rowStart);
    }
}
