package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.cache.EjCache;
import com.abreaking.easyjpa.dao.cache.EjCacheFactory;
import com.abreaking.easyjpa.dao.cache.SelectKey;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.ConnectionHolder;
import com.abreaking.easyjpa.executor.JdbcSqlExecutor;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.builder.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

/**
 * 通用增删改查的模板
 * 它提供了增删改查的通用方法，以及封装了sql的执行过程
 * @author liwei_paas
 * @date 2020/12/13
 */
public class CurdTemplate {

    protected SqlExecutor sqlExecutor;

    public CurdTemplate(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public CurdTemplate(Connection connection) {
        this(new JdbcSqlExecutor(connection));
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
        return doCachesSelect(table,rowMapper,()->sqlBuilder.visit(conditions));
    }

    public void update(String table, Matrix updateMatrix, Conditions conditions){
        SqlBuilder update = new UpdateSqlBuilder(table, updateMatrix);
        doCachesExecute(table,()->update.visit(conditions));
    }

    public void insert(String table, Matrix matrix) {
        SqlBuilder insert = new InsertSqlBuilder(table,matrix);
        doCachesExecute(table,()->insert.visit(null));
    }

    public void delete(String table,Conditions conditions){
        SqlBuilder delete = new DeleteSqlBuilder(table);
        doCachesExecute(table,()->delete.visit(conditions));
    }

    protected List doCachesSelect(String tableName,RowMapper rowMapper,Supplier<PreparedWrapper> supplier){
        ConnectionHolder.setLocalConnection(sqlExecutor.getConnection());
        PreparedWrapper preparedWrapper = supplier.get();
        EjCache defaultCache = EjCacheFactory.getLocalDefaultCache();
        SelectKey selectKey = new SelectKey(preparedWrapper, rowMapper);
        List result = (List) defaultCache.hget(tableName, selectKey);
        try{
            if (result==null){
                result = doSelect(rowMapper,preparedWrapper);
                defaultCache.hput(tableName,selectKey,result);
            }
        }finally {
            ConnectionHolder.removeLocalConnection();
        }
        return result;
    }

    protected void doCachesExecute(String tableName,Supplier<PreparedWrapper> supplier){
        ConnectionHolder.setLocalConnection(sqlExecutor.getConnection());
        PreparedWrapper preparedWrapper = supplier.get();
        EjCache defaultCache = EjCacheFactory.getLocalDefaultCache();
        try{
            doExecute(preparedWrapper);
            defaultCache.remove(tableName);
        }finally {
            ConnectionHolder.removeLocalConnection();
        }

    }

    protected List doSelect(RowMapper rowMapper,PreparedWrapper preparedWrapper){
        String preparedSql = preparedWrapper.getPreparedSql();
        Object[] values = preparedWrapper.getValues();
        int[] types = preparedWrapper.getTypes();
        try {
            return sqlExecutor.query(preparedSql,values,types,rowMapper);
        }catch (SQLException e){
            throw new EasyJpaSqlExecutionException(preparedSql,values,e);
        }
    }

    protected void doExecute(PreparedWrapper preparedWrapper){
        String preparedSql = preparedWrapper.getPreparedSql();
        Object[] values = preparedWrapper.getValues();
        int[] types = preparedWrapper.getTypes();
        try {
            sqlExecutor.execute(preparedSql,values,types);
        }catch (SQLException e){
            throw new EasyJpaSqlExecutionException(preparedSql,values,e);
        }
    }


}
