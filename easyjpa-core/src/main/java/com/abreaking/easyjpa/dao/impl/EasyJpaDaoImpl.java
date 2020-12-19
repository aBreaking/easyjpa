package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.CurdTemplate;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.BaseRowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.PageSqlBuilder;
import com.abreaking.easyjpa.sql.SelectSqlBuilder;
import com.abreaking.easyjpa.util.SqlUtil;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class EasyJpaDaoImpl extends CurdTemplate implements EasyJpaDao {


    public EasyJpaDaoImpl(SqlExecutor sqlExecutor) {
        super(sqlExecutor);
    }

    @Override
    public List query(Object condition) {
        EasyJpa easyJpa = new EasyJpa(condition);
        return select(easyJpa);
    }

    @Override
    public List query(EasyJpa condition) {
        return select(condition);
    }

    @Override
    public Page queryByPage(EasyJpa condition, Page page) {
        try {
            SelectSqlBuilder selectSqlBuilder = new SelectSqlBuilder();
            condition.accept(selectSqlBuilder);
            String conditionPrepareSql = selectSqlBuilder.toPrepareSql();
            Matrix matrix = selectSqlBuilder.toMatrix();
            List<Object[]> query =  sqlExecutor.query("select count(*) from ("+conditionPrepareSql+") _ct",matrix.values(),matrix.types(),new BaseRowMapper(Long.class));
            Object[] totalArray = query.get(0);
            Long total = (Long) totalArray[0];
            List list = doSelect(condition, new PageSqlBuilder(page));
            page.setResult(list);
            //查询出总数

            page.setTotal(total);
            return page;
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    @Override
    public List queryByPrepareSql(String prepareSql, Object[] values,Class type) {
        int[] types = new int[values.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = SqlUtil.getSqlType(values[i].getClass());
        }
        try {
            return sqlExecutor.query(prepareSql,values,types,new BaseRowMapper(type));
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    @Override
    public Object get(Class obj, Object idValue) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.addValues(easyJpa.getIdName(),idValue);
        List list = select(easyJpa);
        return list.isEmpty()?null:list.get(0);
    }

    @Override
    public void update(Object entity) {
        super.update(new EasyJpa(entity));
    }

    @Override
    public void delete(Class obj, Object id) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.addValues(easyJpa.getIdName(),id);
        super.delete(easyJpa);
    }

    @Override
    public void insert(Object entity) {
        super.insert(new EasyJpa(entity));
    }
}
