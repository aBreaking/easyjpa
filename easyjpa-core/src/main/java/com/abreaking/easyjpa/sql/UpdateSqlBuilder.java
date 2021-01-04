package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.StringUtils;


/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class UpdateSqlBuilder extends AbstractSqlBuilder{

    /**
     * execute 的condition 条件
     */
    Conditions conditions ;

    public UpdateSqlBuilder(Conditions conditionList) {
        this.conditions = conditionList;
    }

    public UpdateSqlBuilder() {
    }

    @Override
    protected void doVisit(EasyJpa easyJpa,ColumnMatrix columnMatrix) {
        sqlBuilder.append("UPDATE ");
        sqlBuilder.append(easyJpa.getTableName());
        sqlBuilder.append(" SET ");

        Matrix matrix = easyJpa.matrix();
        String[] columns = matrix.columns();
        if (conditions==null){
            //如果没有指定条件，那么默认按照id 来进行update
            String idName = easyJpa.getIdName();
            if (idName == null){
                throw new NoIdOrPkSpecifiedException(easyJpa.getObj());
            }
            Matrix idMatrix = easyJpa.idMatrix();
            if (idMatrix==null){
                throw new EasyJpaException("execute for "+easyJpa.getObj().getSimpleName()+", no conditions or primary key specified");
            }
            conditions = sqlConst -> sqlConst.equals(SqlConst.AND)?Condition.matrixToCondition(idMatrix):null;

            int[] types = matrix.types();
            Object[] values = matrix.values();
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                if (column.equals(idName)){
                    continue;
                }
                sqlBuilder.append(column);
                sqlBuilder.append("= ?,");
                columnMatrix.put(column,types[i],values[i]);
            }
        }else{
            for (String column : columns){
                sqlBuilder.append(column);
                sqlBuilder.append("= ?,");
            }
            columnMatrix.putAll(matrix);
        }
        StringUtils.cutAtLastSeparator(sqlBuilder,",");
        sqlBuilder.append(" ");
        visitWhere(conditions,columnMatrix);
    }


}
