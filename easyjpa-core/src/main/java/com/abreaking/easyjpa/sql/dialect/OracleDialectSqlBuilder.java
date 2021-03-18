package com.abreaking.easyjpa.sql.dialect;


import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;

/**
 * oracle的方言
 * @author liwei_paas
 * @date 2021/1/4
 */
public class OracleDialectSqlBuilder extends DialectSqlBuilder{

    @Override
    public void visitPage(StringBuilder sqlBuilder,ColumnMatrix columnMatrix, int pageStartIndex, int pageSize) {
        int rowStart = pageStartIndex;
        int rowEnd = pageSize+rowStart;
        sqlBuilder.insert(0,"SELECT * FROM (SELECT ej_tmp.*, ROWNUM ej_rowStart FROM ( ");
        sqlBuilder.append(" ) ej_tmp WHERE ROWNUM <= ?) WHERE ej_rowStart >= ?");
        columnMatrix.put("rowEnd",SqlUtil.getSqlType(Integer.class),rowEnd);
        columnMatrix.put("rowStart",SqlUtil.getSqlType(Integer.class),rowStart);
    }
}
