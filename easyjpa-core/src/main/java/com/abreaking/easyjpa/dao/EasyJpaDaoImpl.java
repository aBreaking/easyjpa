package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.SelectSqlBuilder;
import com.abreaking.easyjpa.sql.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * jdbc 的实现
 * @author liwei_paas
 * @date 2020/11/26
 */
public class EasyJpaDaoImpl<T> implements EasyJpaDao<T>{

    Logger logger = LoggerFactory.getLogger(EasyJpaDaoImpl.class);

    SqlExecutor sqlExecutor;

    public EasyJpaDaoImpl(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public List<T> select(Condition condition) {
        SqlBuilder selectSqlBuilder = new SelectSqlBuilder();
        Matrix matrix = condition.make(selectSqlBuilder);
        String prepareSql = selectSqlBuilder.toSql();
        Object[] values = matrix.values();
        int[] types = matrix.types();
        System.out.println(prepareSql);
        System.out.println(Arrays.toString(values));
        logger.debug(prepareSql);
        logger.debug(Arrays.toString(values));
        try {
            return sqlExecutor.queryForList(prepareSql,values,types,condition);
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    @Override
    public List<T> select(T o) {
        return select(new EasyJpa(o));
    }

    @Override
    public int update(Condition condition) {
        return 0;
    }

    @Override
    public int update(Object o) {
        return update(new EasyJpa(o));
    }

    @Override
    public int delete(Condition condition) {
        return 0;
    }

    @Override
    public int delete(Object o) {
        return delete(new EasyJpa(o));
    }

    @Override
    public int insert(Condition condition) {
        return 0;
    }

    @Override
    public int insert(Object o) {
        return insert(new EasyJpa(o));
    }
}
