package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.CurdTemplate;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Entry;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.BaseRowMapper;
import com.abreaking.easyjpa.sql.PageSqlBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class EasyJpaDaoImpl<T> extends CurdTemplate implements EasyJpaDao<T> {


    public EasyJpaDaoImpl(SqlExecutor sqlExecutor) {
        super(sqlExecutor);
    }

    @Override
    public List<T> query(T condition) {
        EasyJpa<T> easyJpa = new EasyJpa(condition);
        return select(easyJpa);
    }

    @Override
    public List<T> query(EasyJpa condition) {
        return select(condition);
    }

    @Override
    public Page<T> queryByPage(EasyJpa<T> condition, Page page) {
        try {
            List<Object[]> query = sqlExecutor.query("select count(*) from " + condition.getTableName(), new Object[0], new int[0], new BaseRowMapper(Long.class));
            Object[] totalArray = query.get(0);
            Long total = (Long) totalArray[0];
            List<T> list = doSelect(condition, new PageSqlBuilder(page));
            page.setResult(list);
            //查询出总数

            page.setTotal(total);
            return page;
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    @Override
    public T get(Class<T> obj, Object idValue) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.addValues(easyJpa.getIdName(),idValue);
        List<T> list = select(easyJpa);
        return list.isEmpty()?null:list.get(0);
    }

    @Override
    public void update(T entity) {
        super.update(new EasyJpa(entity));
    }

    @Override
    public void delete(Class<T> obj, Object id) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.addValues(easyJpa.getIdName(),id);
        super.delete(easyJpa);
    }

    @Override
    public void insert(T entity) {
        super.insert(new EasyJpa(entity));
    }
}
