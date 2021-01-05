package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Page;

import java.util.List;
import java.util.Map;


/**
 * 枚举可用的curd操作
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

    List<Map<String, Object>> query(String prepareSql, Object...values);

    <T> List<T> query(String prepareSql, Object[] values,Class<T> returnType);

    List<Map<String, Object>> query(String placeholderSql, Map<String, Object> valuesMap);

    <T> List<T> query(String placeholderSql, Map<String, Object> valuesMap,Class<T> returnType);

    List<Map<String, Object>> query(String placeholderSql, Object entity);

    <T> List<T> query(String placeholderSql, Object entity,Class<T> returnType);

    <T> Page<T> queryByPage(EasyJpa<T> condition,Page page);

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
     * @throws com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException 如果该对象没有主键
     * @param entity
     * @return
     */
    <T> void update(T entity) ;

    <T> void update(EasyJpa<T> entity);

    <T> void update(T entity,EasyJpa conditions);

    /**
     * 根据主键删除对象，目前只能根据之间来进行删除
     * @param obj
     * @param id
     * @return
     */
    <T> void delete(Class<T> obj,Object id);

    <T> void delete(EasyJpa conditions);

    /**
     * 新增对象
     * @param entity
     * @return
     */
    <T> void insert(T entity);

    void execute(String prepareSql,Object...values);

    void execute(String placeholderSql,Map<String,Object> valuesMap);
}
