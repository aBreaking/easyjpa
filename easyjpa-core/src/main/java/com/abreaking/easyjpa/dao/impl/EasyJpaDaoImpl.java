package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.CurdTemplate;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.JavaMapRowMapper;
import com.abreaking.easyjpa.sql.PlaceHolderSqlBuilder;
import com.abreaking.easyjpa.sql.PrepareSqlBuilder;

import java.sql.Connection;
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

    public EasyJpaDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List query(Object condition) {
        EasyJpa easyJpa = new EasyJpa(condition);
        return select(easyJpa,easyJpa);
    }

    @Override
    public List query(EasyJpa condition) {
        return select(condition,condition);
    }

    /**
     * 分页
     * @param easyJpa
     * @param page
     * @return
     */
    @Override
    public Page queryByPage(EasyJpa easyJpa, Page page) {
        easyJpa.limit(page.getStartRow(),page.getPageSize());
        List result = query(easyJpa);
        page.setResult(result);

        // 如果已经设置了select，需要保存下之前的select
        List<Condition> conditions = easyJpa.getConditions(SqlConst.SELECT);
        String[] selects = null; //是否已经包含了select
        if (conditions!=null && !conditions.isEmpty()){
            Condition condition = conditions.get(0);
            selects = (String[]) condition.getValues();
        }
        // 该条件下的总数
        easyJpa.remove(SqlConst.LIMIT);
        easyJpa.select("COUNT(*) counter");
        List<Map> list = select(easyJpa, new JavaMapRowMapper());
        Map map = list.get(0);
        Long total = (Long) map.get("counter");
        page.setTotal(total);

        if (selects!=null && selects.length!=0){
            easyJpa.select(selects);
        }

        return page;
    }

    @Override
    public Object get(Class obj, Object idValue) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.and(Condition.equal(easyJpa.getIdName(),idValue));
        List list = query(easyJpa);
        return list.get(0);
    }

    @Override
    public void update(Object entity) {
        super.update(new EasyJpa(entity),null);
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
    public <T> void update(EasyJpa<T> entity) {
        super.update(entity,null);
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
