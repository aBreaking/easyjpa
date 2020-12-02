package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.dao.Condition;
import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.EasyJpaTemplate;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.mapper.ClassMatrixRowMapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.MatrixUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * @author liwei_paas
 * @date 2020/12/2
 */
public class EasyJpaTemplateImpl<T> implements EasyJpaTemplate<T> {

    EasyJpaDao dao;

    public EasyJpaTemplateImpl(EasyJpaDao easyJpaDao) {
        this.dao = easyJpaDao;
    }

    @Override
    public List<T> query(T condition) {
        EasyJpa<T> easyJpa = new EasyJpa<>(condition);
        return query(easyJpa);
    }

    @Override
    public List<T> query(EasyJpa<T> condition) {
        return dao.select(condition);
    }

    @Override
    public T get(Class<T> obj, Object id) {
        ClassMatrixRowMapper map = ClassMatrixRowMapper.map(obj);
        Matrix matrix = map.mapId();
        ColumnMatrix columnMatrix = MatrixUtil.addValue(matrix, id);
        EasyJpa<T> easyJpa = new EasyJpa<>(obj, columnMatrix);
        List<T> list = dao.select(easyJpa);
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
        EasyJpa easyJpa = new EasyJpa(entity);
        Field id = easyJpa.id();
        id.setAccessible(true);
        Object value = null;
        try {
            value = id.get(entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (value == null){
            throw new EasyJpaException("No primary key value specified");
        }
        EasyJpa idJpa = new EasyJpa(entity.getClass());
        idJpa.addValues(id.getName(),value);
        return update(easyJpa,idJpa);
    }

    @Override
    public int delete(Class obj, Object id) {
        EasyJpa easyJpa = new EasyJpa<>(obj);
        Field idField = easyJpa.id();
        easyJpa.addValues(idField.getName(),id);
        return delete(easyJpa);
    }

    @Override
    public int insert(T entity) {
        return dao.insert(new EasyJpa<>(entity));
    }

    @Override
    public List select(Condition condition) {
        return dao.select(condition);
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
