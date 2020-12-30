package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.CurdTemplate;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.JavaMapRowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.PlaceHolderSqlBuilder;
import com.abreaking.easyjpa.sql.PrepareSqlBuilder;
import com.abreaking.easyjpa.sql.SelectSqlBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
            condition.limit(page.getPageNum(),page.getPageSize());
            SelectSqlBuilder selectSqlBuilder = new SelectSqlBuilder();
            Matrix matrix = selectSqlBuilder.visit(condition);
            List result = select(condition);
            page.setResult(result);
            String conditionPrepareSql = selectSqlBuilder.toString();
            List<Object[]> query =  sqlExecutor.query("select count(*) from ("+conditionPrepareSql+") _ct",matrix.values(),matrix.types(),new JavaMapRowMapper());
            Object[] totalArray = query.get(0);
            Long total = (Long) totalArray[0];
            page.setTotal(total);
            return page;
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    @Override
    public Object get(Class obj, Object idValue) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.and(Condition.equal(easyJpa.getIdName(),idValue));
        List list = super.select(easyJpa);
        return list.get(0);
    }

    @Override
    public void update(Object entity) {
        super.update(new EasyJpa(entity));
    }

    @Override
    public void delete(Class obj, Object id) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.and(Condition.equal(easyJpa.getIdName(),id));
        super.delete(easyJpa);
    }


    @Override
    public void delete(EasyJpa easyJpa) {
        super.delete(easyJpa);
    }

    @Override
    public void insert(Object entity) {
        super.insert(new EasyJpa(entity));
    }

    @Override
    public List<Map<String, Object>> query(String prepareSql, Object[] values) {
        return doSelect(new PrepareSqlBuilder(prepareSql,values),null,new JavaMapRowMapper());
    }

    @Override
    public  List query(String prepareSql, Object[] values,Class returnType) {
        return doSelect(new PrepareSqlBuilder(prepareSql,values),null,new EasyJpa(returnType));
    }

    @Override
    public List<Map<String, Object>> query(String placeholderSql, Map<String, Object> params) {
        return doSelect(new PlaceHolderSqlBuilder(placeholderSql, params),null,new JavaMapRowMapper());
    }
    
    @Override
    public  List query( String placeholderSql, Map params, Class returnType) {
        EasyJpa easyJpa = new EasyJpa(returnType);
        return doSelect(new PlaceHolderSqlBuilder(placeholderSql, params),easyJpa,easyJpa);
    }

    @Override
    public List<Map<String, Object>> query(String placeholderSql, Object entity) {
        return doSelect(new PlaceHolderSqlBuilder(placeholderSql, entity),new EasyJpa(entity.getClass()),new JavaMapRowMapper());
    }



    @Override
    public <T> void update(T entity, EasyJpa condition) {
        super.update(new EasyJpa(entity),condition);
    }

    @Override
    public void execute(String prepareSql, Object[] values) {
        doExecute(new PrepareSqlBuilder(prepareSql,values),null);
    }

    @Override
    public void execute(String placeholderSql, Map<String, Object> valuesMap) {
        doExecute(new PlaceHolderSqlBuilder(placeholderSql,valuesMap),null);
    }

    @Override
    public List query(String placeholderSql, Object entity,Class returnType) {
        EasyJpa easyJpa = new EasyJpa(returnType);
        return doSelect(new PlaceHolderSqlBuilder(placeholderSql, entity),easyJpa,easyJpa);
    }
}
