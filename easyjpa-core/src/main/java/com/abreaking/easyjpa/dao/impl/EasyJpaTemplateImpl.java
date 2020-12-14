/*
package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.EasyJpaTemplate;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.mapper.ClassRowMapper;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.util.List;

*/
/**
 *
 * @author liwei_paas
 * @date 2020/12/2
 *//*

public class EasyJpaTemplateImpl<T> implements EasyJpaTemplate<T> {

    EasyJpaDao dao;

    public EasyJpaTemplateImpl(EasyJpaDao easyJpaDao) {
        this.dao = easyJpaDao;
    }

    @Override
    public List<T> query(T condition) {
        EasyJpa<T> easyJpa = new EasyJpa<>(condition);
        return select(easyJpa,condition.getClass());
    }

    @Override
    public List<T> query(EasyJpa<T> condition) {
        return select(condition,condition.getObj());
    }

    @Override
    public Page<T> queryByPage(EasyJpa<T> condition, Page page) {
        return null;
    }

    @Override
    public T get(Class<T> obj, Object idValue) {
        EasyJpa<T> easyJpa = new EasyJpa(obj);
        String idName = easyJpa.idColumn();
        easyJpa.addValues(idName,idValue);
        List<T> list = select(easyJpa,obj);
        if (list.isEmpty()){
            return null;
        }
        if (list.size() > 2){
            throw new EasyJpaSqlExecutionException("The primary key query can only return one result, but more than two records are found");
        }
        return list.get(0);
    }

    @Override
    public int update(T entity) {
        EasyJpa set = new EasyJpa(entity);
        EasyJpa idCondition = new EasyJpa(entity.getClass());
        String column = idCondition.idColumn();
        Object value = set.getValue(column);
        if (value == null){
            throw new EasyJpaException("No primary key value specified");
        }
        idCondition.addValues(column,value);
        return update(set,idCondition);
    }

    @Override
    public int delete(Class obj, Object id) {
        EasyJpa easyJpa = new EasyJpa<>(obj);
        String idColumn = easyJpa.idColumn();
        easyJpa.addValues(idColumn,id);
        return delete(easyJpa);
    }

    @Override
    public int insert(T entity) {
        return dao.insert(new EasyJpa<>(entity));
    }

    private List<T> select(Condition condition,Class obj){
        return select(condition,new ClassRowMapper(obj));
    }

    @Override
    public List<T> select(Condition condition, RowMapper rowMapper) {
        return dao.select(condition,rowMapper);
    }

    @Override
    public int update(Condition set, Condition condition) {
        return dao.update(set,condition);
    }

    @Override
    public int delete(Condition condition) {
        return dao.delete(condition);
    }

    @Override
    public int insert(Condition condition) {
        return dao.insert(condition);
    }
}
*/
