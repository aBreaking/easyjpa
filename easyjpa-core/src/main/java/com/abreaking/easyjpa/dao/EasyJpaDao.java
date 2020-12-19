package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;

import java.util.List;


/**
 *
 * @author liwei_paas
 * @date 2020/12/2
 */
public interface EasyJpaDao {

    /**
     * 将对象作为一个条件，进行条件查询
     * @param condition
     * @return
     */
    <T> List<T> query(T condition);

    /**
     * 传入自定义的条件，进行条件查询
     * 该方法可以使用select方法取代
     * @param condition
     * @return
     */
    <T> List<T> query(EasyJpa<T> condition);

    <T> Page<T> queryByPage(EasyJpa<T> condition,Page page);

    /**
     * 根据prepareSql查询
     * 返回 结果集的对象
     * @param prepareSql
     * @param values
     * @return
     */
    <T> List<T[]> queryByPrepareSql(String prepareSql,Object[] values,Class<T> type);

    /**
     * 根据主键直接查询单个结果对象
     * @param obj
     * @param idValue
     * @return
     */
    <T> T get(Class<T> obj,Object idValue);

    /**
     * 更新某个对象，只能只能是根据单个对象的主键来进行update操作
     * 即先根据主键找到该记录，然后根据entity的其他值 更新除主键外的值
     * @throws NoIdOrPkSpecifiedException 如果该对象没有主键
     * @param entity
     * @return
     */
    <T> void update(T entity) ;

    /**
     * 根据主键删除对象，目前只能根据之间来进行删除
     * @param obj
     * @param id
     * @return
     */
    <T> void delete(Class<T> obj,Object id);

    /**
     * 新增对象
     * @param entity
     * @return
     */
    <T> void insert(T entity);
}
