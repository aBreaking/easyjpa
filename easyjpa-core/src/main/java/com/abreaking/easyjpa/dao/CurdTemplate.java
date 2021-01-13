package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.config.EasyJpaConfiguration;
import com.abreaking.easyjpa.dao.cache.EjCache;
import com.abreaking.easyjpa.dao.cache.EjCacheFactory;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.ConnectionHolder;
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
     * @param jpa
     * @param rowMapper
     * @return
     */
    public List<T> select(EasyJpa jpa,RowMapper<T> rowMapper) {
        // 应该在此处进行缓存
        ConnectionHolder holder = sqlExecutor.getConnectionHolder();
        try{
            if (holder.getConnection().getAutoCommit()){
                return (List<T>) cache.getIfAbsent(jpa,rowMapper,()->doSelect(new SelectSqlBuilder(),jpa,rowMapper));
            }else{
                return doSelect(new SelectSqlBuilder(), jpa, rowMapper);
            }
        }catch (SQLException e){
            throw new EasyJpaSqlExecutionException(e);
        }

    }

    public void update(EasyJpa jpa,Conditions conditions){
        cache.remove(jpa);
        doExecute(new UpdateSqlBuilder(conditions),jpa);
    }

    public void insert(EasyJpa jpa) {
        cache.remove(jpa);
        doExecute(new InsertSqlBuilder(),jpa);
    }

    public void delete(EasyJpa jpa){
        cache.remove(jpa);
        doExecute(new DeleteSqlBuilder(),jpa);
    }

    /**
     * 指定SqlBuilder，easyJpa作为查询条件，组装sql，并执行。最后返回rowMapper的封装对象
     * @param sqlBuilder
     * @param easyJpa
     * @param rowMapper
     * @return
     */
    protected List<T> doSelect(SqlBuilder sqlBuilder,EasyJpa easyJpa,RowMapper rowMapper) {
        Matrix matrix = sqlBuilder.visit(easyJpa);
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
            sqlExecutor.execute(prepareSql,values,types);
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(prepareSql,values,e);
        }
    }
}
