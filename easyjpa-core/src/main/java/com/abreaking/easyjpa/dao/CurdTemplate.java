package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 通用增删改查的模板
 * @author liwei_paas
 * @date 2020/12/13
 */
public class CurdTemplate<T> {

    protected SqlExecutor sqlExecutor;

    public CurdTemplate(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public List<T> select(EasyJpa jpa,RowMapper<T> rowMapper) {
        SqlBuilder sqlBuilder = new SelectSqlBuilder();
        Matrix matrix = sqlBuilder.visit(jpa);
        return doSelect(sqlBuilder,matrix,rowMapper);
    }

    public List<T> select(EasyJpa jpa) {
        return select(jpa,jpa);
    }

    public void update(EasyJpa jpa) {
        doExecute(new UpdateSqlBuilder(),jpa);
    }

    public void update(EasyJpa jpa,Conditions conditions){
        doExecute(new UpdateSqlBuilder(conditions),jpa);
    }

    public void insert(EasyJpa jpa) {
        doExecute(new InsertSqlBuilder(),jpa);
    }

    public void delete(EasyJpa jpa){
        doExecute(new DeleteSqlBuilder(),jpa);
    }

    protected List<T> doSelect(SqlBuilder sqlBuilder,Matrix matrix,RowMapper rowMapper) {
        String prepareSql = sqlBuilder.toString();
        Object[] values = matrix.values();
        int[] types = matrix.types();
        try {
            System.out.println(prepareSql+"\n"+Arrays.toString(values));
            return sqlExecutor.query(prepareSql,values,types,rowMapper);
        }catch (SQLException e){
            throw new EasyJpaSqlExecutionException(prepareSql,values,e);
        }
    }

    protected void doExecute(SqlBuilder sqlBuilder,EasyJpa easyJpa){
        Matrix matrix = sqlBuilder.visit(easyJpa);
        String prepareSql = sqlBuilder.toString();
        Object[] values = matrix.values();
        int[] types = matrix.types();
        try {
            System.out.println(prepareSql+"\n"+Arrays.toString(values));
            sqlExecutor.update(prepareSql,values,types);
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(prepareSql,values,e);
        }
    }
}
