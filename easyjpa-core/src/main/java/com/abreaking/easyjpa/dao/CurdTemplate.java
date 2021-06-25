package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.executor.*;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.builder.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * 通用增删改查的模板
 * 它提供了增删改查的通用方法，以及封装了sql的执行过程
 * @author liwei_paas
 * @date 2020/12/13
 */
public class CurdTemplate {

    protected EasyJpaExecutor executor;

    public CurdTemplate(EasyJpaExecutor executor) {
        this.executor = executor;
    }

    public CurdTemplate(Connection connection) {
        this.executor = new EasyJpaJdbcExecutor(connection);
    }

    public CurdTemplate(DataSource dataSource) {
        this.executor = new EasyJpaDataSourceExecutor(dataSource);
    }


    /**
     * 通用的查询，根据条件查询，返回rowMapper封装的对象
     * @param table
     * @param rowMapper
     * @param conditions
     * @return
     */
    public <T> List<T> select(String table, Conditions conditions,RowMapper<T> rowMapper) {
        SelectSqlBuilder sqlBuilder = new SelectSqlBuilder(table);
        return executor.query(()->sqlBuilder.visit(conditions),rowMapper);
    }

    public void update(String table, Matrix updateMatrix, Conditions conditions){
        SqlBuilder update = new UpdateSqlBuilder(table, updateMatrix);
        executor.execute(()->update.visit(conditions));
    }

    public void insert(String table, Matrix matrix) {
        SqlBuilder insert = new InsertSqlBuilder(table,matrix);
        executor.execute(()->insert.visit(null));
    }

    public void delete(String table,Conditions conditions){
        SqlBuilder delete = new DeleteSqlBuilder(table);
        executor.execute(()->delete.visit(conditions));
    }

}
