package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.builder.prepare.PlaceholderWrapper;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.support.EasyJpa;

import java.util.List;


/**
 * 可用的curd操作
 * @author liwei_paas
 * @date 2020/12/2
 */
public interface EasyJpaDao {

    /**
     * 根据主键询单个结果对象。如果实体类没有指定主键，那么会抛出NoIdOrPkSpecifiedException异常
     * @param obj 实体
     * @param idValue 主键值
     * @param <T>
     * @return
     * @throws NoIdOrPkSpecifiedException 如果实体类没有指定主键
     */
    <T> T get(Class<T> obj,Object idValue) throws NoIdOrPkSpecifiedException;

    /**
     * 将对象本身就视为查询条件，进行条件查询。
     * 它只会是全字匹配，即类似：query * from table where column1=? and column2=?
     * @param condition
     * @return
     */
    <T> List<T> query(T condition);

    /**
     * 自定义条件查询。可以使用包括like limit orderby or 等sql关键字的条件 来进行匹配
     * @param condition
     * @return
     */
    <T> List<T> queryByCondition(EasyJpa<T> condition);

    /**
     * 自定义查询条件以及返回类型
     * @param condition 查询条件
     * @param rowMapper 返回类型处理
     * @param <T>
     * @return
     */
    <T> List<T> queryByCondition(EasyJpa<T> condition,RowMapper rowMapper);

    /**
     * 统一封装的分页查询。
     * 与EasyJpa里自带的limit orderby 分页方法不同的是，Page封装了返回可直接与前端交互的查询结果，包括分页结果、总页数、总记录数等。
     *  而单独使用EasyJpa进行的条件查询只返回分页结果
     * @param condition
     * @param page
     * @param <T>
     * @return
     */
    <T> Page<T> queryByPage(EasyJpa<T> condition,Page page);

    /**
     * 更新某个对象，只能是根据单个对象的主键来进行update操作
     * 即先根据主键找到该记录，然后根据entity的其他值 更新除主键外的值
     * @throws com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException 如果该对象没有主键
     * @param entity
     * @return
     */
    <T> void update(T entity) throws NoIdOrPkSpecifiedException;

    /**
     * 根据指定的条件conditions 来修改entity
     * @param entity 要修改的内容
     * @param conditions 指定条件
     * @param <T> 实体
     */
    <T> void updateByCondition(T entity,Conditions conditions);

    /**
     * 根据主键删除对象，目前只能根据之间来进行删除
     * @param obj
     * @param id
     * @return
     */
    <T> void deleteById(Class<T> obj,Object id);

    /**
     * 根据条件删除
     * @param conditions
     * @param <T>
     */
    <T> void deleteByCondition(EasyJpa<T> conditions);

    /**
     * 新增对象
     * @param entity
     * @return
     */
    <T> void insert(T entity);

    <T> void insertBatch(List<T> list);

    <T> List<T> query(PlaceholderWrapper placeholderSql, RowMapper<T> resultRowMapper);

    <T> List<T> query(PreparedWrapper preparedSql, RowMapper<T> resultRowMapper);

    void execute(PlaceholderWrapper placeholderWrapper);

    void execute(PreparedWrapper preparedSql);

}
