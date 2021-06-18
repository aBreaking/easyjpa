package com.abreaking.easyjpa.dao.impl;

import com.abreaking.easyjpa.builder.PlaceHolderSqlBuilder;
import com.abreaking.easyjpa.builder.prepare.PlaceholderWrapper;
import com.abreaking.easyjpa.dao.condition.SqlConst;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;
import com.abreaking.easyjpa.mapper.ClassRowMapper;
import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.CurdTemplate;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.support.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.JavaMapRowMapper;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.builder.ConditionBuilderDelegate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
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

    public EasyJpaDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List query(Object condition) {
        EasyJpa easyJpa = new EasyJpa(condition);
        return queryByCondition(easyJpa);
    }

    @Override
    public <T> List<T> queryByCondition(EasyJpa<T> condition) {
        return super.select(condition.getTableName(),condition,new ClassRowMapper(condition.getObj()));
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
        List result = queryByCondition(easyJpa);
        page.setResult(result);

        // 该条件下的总数,也应该考虑将其缓存
        String tableName = easyJpa.getTableName();

        List<Map> list = doCachesSelect(tableName,new JavaMapRowMapper(),()->{
            StringBuilder counterBuilder = new StringBuilder("SELECT COUNT(*) COUNTER FROM ");
            counterBuilder.append(tableName);
            ColumnMatrix matrix = new AxisColumnMatrix();
            ConditionBuilderDelegate delegate = new ConditionBuilderDelegate(easyJpa);
            delegate.visitWhere(counterBuilder,matrix);
            return new PreparedWrapper(counterBuilder.toString(), matrix);
        });

        Map map = list.get(0);
        Object counter = map.get("COUNTER");
        long total = new Double(String.valueOf(counter)).longValue();
        page.setTotal(total);

        return page;
    }

    @Override
    public Object get(Class obj, Object idValue) {
        EasyJpa easyJpa = new EasyJpa(obj);
        String idColumnName = easyJpa.getIdColumnName();
        if (idColumnName==null){
            throw new NoIdOrPkSpecifiedException(obj);
        }
        easyJpa.and(Condition.equal(idColumnName,idValue));
        List list = queryByCondition(easyJpa);
        return list.isEmpty()?null:list.get(0);
    }

    @Override
    public void update(Object entity) {
        //需要考虑将主键作为条件，进而进行update
        EasyJpa easyJpa = new EasyJpa(entity);
        String idColumnName = easyJpa.getIdColumnName();
        if (idColumnName == null) {
            throw new NoIdOrPkSpecifiedException(easyJpa.getObj());
        }
        super.update(easyJpa.getTableName(),easyJpa.matrix(),sqlConst->{
            //默认将主键作为修改条件
            Object value = easyJpa.getValue(idColumnName);
            if(value==null){
                throw new EasyJpaException("the object of "+easyJpa.getObj()+" must have id value");
            }
            return sqlConst==SqlConst.AND ? Collections.singletonList(Condition.equal(idColumnName,value)):null;
        });
    }

    public  void updateByCondition(Object entity, Conditions conditions) {
        EasyJpa easyJpa = new EasyJpa(entity);
        super.update(easyJpa.getTableName(),easyJpa.matrix(),conditions);
    }


    @Override
    public void insert(Object entity) {
        EasyJpa easyJpa = new EasyJpa(entity);
        super.insert(easyJpa.getTableName(),easyJpa.matrix());
    }


    @Override
    public <T> void deleteById(Class<T> obj, Object id) {
        EasyJpa easyJpa = new EasyJpa(obj);
        super.delete(easyJpa.getTableName(),(sqlConst)->Collections.singletonList(Condition.equal(easyJpa.getIdColumnName(),id)));
    }

    @Override
    public <T> void deleteByCondition(EasyJpa<T> conditions) {
        super.delete(conditions.getTableName(),conditions);
    }

    @Override
    public <T> void insertBatch(List<T> list) {
        if (list.isEmpty()){
            return;
        }
        //使用事务，批量insert
        Connection connection = sqlExecutor.getConnection();
        Boolean autoCommit = null;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            for (T t : list){
                EasyJpa easyJpa = new EasyJpa(t);
                super.insert(easyJpa.getTableName(),easyJpa.matrix());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new EasyJpaException(e1);
            }
            throw new EasyJpaSqlExecutionException("batch insert failed",e);
        }finally {
            if (autoCommit!=null){
                try {
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    throw new EasyJpaException(e);
                }
            }
        }
    }

    @Override
    public <T> List<T> query(PlaceholderWrapper placeholderSql, RowMapper<T> resultRowMapper) {
        PlaceHolderSqlBuilder placeHolderSqlBuilder = new PlaceHolderSqlBuilder(placeholderSql);
        PreparedWrapper preparedWrapper = placeHolderSqlBuilder.visit(null);
        return doSelect(resultRowMapper,preparedWrapper);
    }

    @Override
    public <T> List<T> query(PreparedWrapper preparedSql, RowMapper<T> resultRowMapper) {
        return doSelect(resultRowMapper,preparedSql);
    }

    @Override
    public void execute(PlaceholderWrapper placeholderWrapper) {
        doExecute(new PlaceHolderSqlBuilder(placeholderWrapper).visit(null));
    }

    @Override
    public void execute(PreparedWrapper preparedSql) {
        doExecute(preparedSql);
    }
}
