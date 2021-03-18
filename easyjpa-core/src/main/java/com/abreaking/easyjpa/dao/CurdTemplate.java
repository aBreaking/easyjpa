package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.config.EasyJpaConfiguration;
import com.abreaking.easyjpa.dao.cache.CacheKey;
import com.abreaking.easyjpa.dao.cache.EjCache;
import com.abreaking.easyjpa.dao.cache.EjCacheFactory;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.prepare.PreparedWrapper;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.JdbcSqlExecutor;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 通用增删改查的模板
 * 它提供了增删改查的通用方法，以及封装了sql的执行过程。它一般由子类去实现，并且其子类应该被使用成单例模式。
 * @author liwei_paas
 * @date 2020/12/13
 */
public class CurdTemplate<T> {

    protected SqlExecutor sqlExecutor;

    protected EjCache cache;

    public CurdTemplate(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
        this.cache = EjCacheFactory.getDefaultCache(sqlExecutor);
        EasyJpaConfiguration.setDialectWithConnection(sqlExecutor.getConnectionHolder());
    }

    public CurdTemplate(Connection connection){
        this(new JdbcSqlExecutor(connection));
    }

    /**
     * 通用的查询，根据条件查询，返回rowMapper封装的对象
     * @param table
     * @param rowMapper
     * @param conditions
     * @return
     */
    public List<T> select(String table, Conditions conditions,RowMapper<T> rowMapper) {
        SqlBuilder sqlBuilder = new SelectSqlBuilder(table);
        PreparedWrapper preparedWrapper = sqlBuilder.visit(conditions);
        return doCacheablesSelect(table,preparedWrapper,rowMapper);
    }

    public void update(String table, Matrix updateMatrix, Conditions conditions){
        SqlBuilder update = new UpdateSqlBuilder(table, updateMatrix);
        PreparedWrapper preparedWrapper = update.visit(conditions);
        doExecute(preparedWrapper);
        cache.remove(table);
    }

    public void insert(String table, Matrix matrix) {
        SqlBuilder sqlBuilder = new InsertSqlBuilder(table,matrix);
        doExecute(sqlBuilder.visit(null));
        cache.remove(table);
    }

    public void delete(String table,Conditions conditions){
        SqlBuilder sqlBuilder = new DeleteSqlBuilder(table);
        PreparedWrapper preparedWrapper = sqlBuilder.visit(conditions);
        doExecute(preparedWrapper);
        cache.remove(table);
    }

    protected List<T> doCacheablesSelect(String table, PreparedWrapper preparedWrapper, RowMapper rowMapper){
        CacheKey cacheKey = new CacheKey(preparedWrapper, rowMapper);
        return (List<T>) cache.hgetOrHputIfAbsent(table,cacheKey,()->doSelect(preparedWrapper,rowMapper));
    }

    /**
     * 指定SqlBuilder，easyJpa作为查询条件，组装sql，并执行。最后返回rowMapper的封装对象
     * @param preparedWrapper sql执行的参数
     * @param rowMapper
     * @return
     */
    protected List<T> doSelect(PreparedWrapper preparedWrapper,RowMapper rowMapper) {
        String preparedSql = preparedWrapper.getPreparedSql();
        Object[] values = preparedWrapper.getValues();
        int[] types = preparedWrapper.getTypes();
        try {
            return sqlExecutor.query(preparedSql,values,types,rowMapper);
        }catch (SQLException e){
            throw new EasyJpaSqlExecutionException(preparedSql,values,e);
        }
    }

    /**
     *
     * @author liwei
     * @date 2021/3/1
     */
    protected void doExecute(PreparedWrapper preparedWrapper){
        String prepareSql = preparedWrapper.getPreparedSql();
        Object[] values = preparedWrapper.getValues();
        int[] types = preparedWrapper.getTypes();
        try {
            sqlExecutor.execute(prepareSql,values,types);
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(prepareSql,values,e);
        }
    }
}
