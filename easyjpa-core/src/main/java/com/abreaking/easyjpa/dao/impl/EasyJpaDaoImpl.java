package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.dao.prepare.PreparedMapper;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.CurdTemplate;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.dao.prepare.PreparedTypeMapper;
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
        List list = select(easyJpa,easyJpa);
        return list.get(0);
    }

    @Override
    public void update(Object entity) {
        super.update(new EasyJpa(entity),null);
    }


    @Override
    public void insert(Object entity) {
        super.insert(new EasyJpa(entity));
    }

    @Override
    public <T> List<T> queryByCondition(EasyJpa<T> condition) {
        return select(condition,condition);
    }

    @Override
    public List queryByPreparedSql(PreparedMapper prepared, Class...returnType) {
        return doSelect(new PrepareSqlBuilder(prepared),null,new PreparedTypeMapper(returnType));
    }

    @Override
    public List queryByPlaceholderSql(PlaceholderMapper placeholder, Class... returnType) {
        return doSelect(new PlaceHolderSqlBuilder(placeholder),null,new PreparedTypeMapper(returnType));
    }

    @Override
    public <T> void updateByCondition(T entity, EasyJpa conditions) {
        update(new EasyJpa(entity),conditions);
    }

    @Override
    public void delete(Object t) {
        super.delete(new EasyJpa(t));
    }

    @Override
    public <T> void deleteById(Class<T> obj, Object id) {
        EasyJpa easyJpa = new EasyJpa(obj);
        easyJpa.and(Condition.equal(easyJpa.getIdName(),id));
        super.delete(easyJpa);
    }

    @Override
    public <T> void deleteByCondition(EasyJpa<T> conditions) {
        super.delete(conditions);
    }

    @Override
    public void executePrepareSql(PreparedMapper preparedMapper) {
        doExecute(new PrepareSqlBuilder(preparedMapper),null);
    }

    @Override
    public void executePlaceholderSql(PlaceholderMapper placeholderMapper) {
        doExecute(new PlaceHolderSqlBuilder((placeholderMapper)),null);
    }
}
