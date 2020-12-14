/*
package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.ClassRowMapper;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.*;
import com.abreaking.easyjpa.util.SqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

*/
/**
 * jdbc 的实现
 * @author liwei_paas
 * @date 2020/11/26
 *//*

public class EasyJpaDaoImpl<T> implements EasyJpaDao<T> {

    Logger logger = LoggerFactory.getLogger(EasyJpaDaoImpl.class);

    SqlExecutor sqlExecutor;

    public EasyJpaDaoImpl(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public List<T> select(Condition condition, RowMapper rowMapper) {
        SqlBuilder selectSqlBuilder = new SelectSqlBuilder();
        Matrix matrix = condition.make(selectSqlBuilder);
        String prepareSql = selectSqlBuilder.toSql();
        Object[] values = matrix.values();
        int[] types = matrix.types();
        if (logger.isDebugEnabled()){
            logger.debug(prepareSql);
            logger.debug(Arrays.toString(values));
        }
        try {
            return sqlExecutor.query(prepareSql,values,types,rowMapper);
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    public int update(Condition set,Condition condition) {
        UpdateSqlBuilder sqlBuilder = new UpdateSqlBuilder();
        Matrix setMatrix = set.make(sqlBuilder);
        sqlBuilder.setWhere(true);
        Matrix conditionMatrix = condition.make(sqlBuilder);
        Object[] setValues = setMatrix.values();
        Object[] conditionValues = conditionMatrix.values();
        Object[] values = new Object[setValues.length+conditionValues.length];
        int[] types = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            if (i<setValues.length){
                values[i] = setValues[i];
                types[i] = setMatrix.types()[i];
            }else{
                int j = i-setValues.length;
                values[i] = conditionValues[j];
                types[i] = conditionMatrix.types()[j];
            }
        }
        try {
            return sqlExecutor.update(sqlBuilder.toSql(),values,types);
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }


    @Override
    public int delete(Condition condition) {
        return doUpdate(condition,new DeleteSqlBuilder());
    }


    @Override
    public int insert(Condition condition) {
        return doUpdate(condition,new InsertSqlBuilder());
    }

    private int doUpdate(Condition condition,SqlBuilder sqlBuilder){
        Matrix matrix = condition.make(sqlBuilder);
        String prepareSql = sqlBuilder.toSql();
        Object[] values = matrix.values();
        int[] types = matrix.types();
        try {
            return sqlExecutor.update(prepareSql,values,types);
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    public Page<T> queryByPage(EasyJpa<T> condition, Page page) {
        SelectSqlBuilder selectSqlBuilder = new SelectSqlBuilder();
        ColumnMatrix matrix = (ColumnMatrix) condition.make(selectSqlBuilder);
        String prepareSql = selectSqlBuilder.toSql();
        if (page.getOrderBy()!=null){
            String orderBy = page.getOrderBy();
            prepareSql += " order by ?";
            matrix.put("order",SqlUtil.getSqlType(String.class),orderBy);
        }
        int start = page.getStartRow();
        int end = page.getEndRow();
        prepareSql += " limit ?,?";
        matrix.put("limit1",SqlUtil.getSqlType(Integer.class),start);
        matrix.put("limit2",SqlUtil.getSqlType(Integer.class),end);
        try {
            List list = sqlExecutor.query(prepareSql, matrix.values(), matrix.types(), new ClassRowMapper(condition.getObj()));
            page.setResult(list);
            return page;
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

}
*/
