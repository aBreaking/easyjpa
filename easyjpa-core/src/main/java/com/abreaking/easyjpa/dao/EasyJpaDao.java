package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.dao.prepare.PreparedMapper;
import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;

import java.util.List;


/**
 * 枚举可用的curd操作
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
     * 它只会是全字匹配，即类似：select * from table where column1=? and column2=?
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
     * 预处理的sql查询
     * @param prepared
     * @return
     */
    List queryByPreparedSql(PreparedMapper prepared,Class...returnType);

    /**
     * 占位符sql查询
     * @param placeholder
     * @param returnType
     * @return
     */
    List queryByPlaceholderSql(PlaceholderMapper placeholder,Class...returnType);

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
    <T> void updateByCondition(T entity,EasyJpa conditions);

    /**
     * 根据实体的数据删除对应的记录
     * @param entity
     * @param <T>
     */
    <T> void delete(T entity);

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

    void executePrepareSql(PreparedMapper preparedMapper);

    void executePlaceholderSql(PlaceholderMapper placeholderMapper);
}
