package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.config.Configuration;
import com.abreaking.easyjpa.dao.cache.EjCache;
import com.abreaking.easyjpa.dao.cache.EjCacheFactory;
import com.abreaking.easyjpa.dao.cache.SelectKey;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Supplier;


/**
 * 通用的jdbc sql查询方法
 * @author liwei
 * @date 2021/6/25
 */
public class EasyJpaJdbcExecutor implements EasyJpaExecutor{

    Connection connection;

    //是否开启缓存
    private Boolean useCache = Boolean.valueOf(Configuration.useCache.getConfig());

    //是否打印sql，目前先暂时sout输出，后续补上日志
    private Boolean showSql = Boolean.valueOf(Configuration.showSql.getConfig());

    public EasyJpaJdbcExecutor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T> List<T> query(Supplier<PreparedWrapper> sqlSupplier, RowMapper rowMapper) {
        try{
            ConnectionHolder.setLocalConnection(connection);
            PreparedWrapper preparedWrapper = sqlSupplier.get();
            List result ;
            String tableName = preparedWrapper.getTableName();
            if (useCache && tableName!=null){
                EjCache defaultCache = EjCacheFactory.getLocalDefaultCache();
                SelectKey selectKey = new SelectKey(preparedWrapper, rowMapper);
                result = (List) defaultCache.hget(tableName, selectKey);
                if (result==null){
                    result = doQuery(preparedWrapper,rowMapper);
                    defaultCache.hput(tableName,selectKey,result);
                }
            }else{
                result = doQuery(preparedWrapper,rowMapper);
            }

            return result;
        } finally {
            ConnectionHolder.removeLocalConnection();
        }
    }

    public void execute(Supplier<PreparedWrapper> sqlSupplier) {
        try{
            ConnectionHolder.setLocalConnection(connection);
            PreparedWrapper preparedWrapper = sqlSupplier.get();
            doExecute(preparedWrapper);
            String tableName = preparedWrapper.getTableName();
            if (useCache && tableName!=null){
                EjCache defaultCache = EjCacheFactory.getLocalDefaultCache();
                defaultCache.remove(tableName);
            }
        } finally {
            ConnectionHolder.removeLocalConnection();
        }
    }

    protected  <T> List<T> doQuery(PreparedWrapper preparedWrapper, RowMapper<T> rowMapper)  {
        PreparedStatement ps  = null;
        ResultSet rs = null;
        try{
            String preparedSql = preparedWrapper.getPreparedSql();
            Object[] values = preparedWrapper.getValues();
            int[] types = preparedWrapper.getTypes();
            if (showSql){
                System.out.println(preparedWrapper);
            }
            ps = connection.prepareStatement(preparedSql);
            for (int i = 0; i < values.length; i++) {
                setValue(ps,i+1,types[i],values[i]);
            }
            rs = ps.executeQuery();
            List<T> list = new ArrayList<>();
            int i =0;
            while (rs.next()){
                Object o = rowMapper.mapRow(rs, i);
                i++;
                list.add((T) o);
            }
            return list;
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException("query sql failed, preparedSql :"+preparedWrapper.toString(),e);
        } finally {
            try{
                if (rs!=null){
                    rs.close();
                }
                if (ps!=null){
                    ps.close();
                }
            }catch (SQLException e){
                throw new EasyJpaException(e);
            }

        }
    }

    protected int doExecute(PreparedWrapper preparedWrapper) {
        PreparedStatement ps = null;
        try{
            String preparedSql = preparedWrapper.getPreparedSql();
            Object[] values = preparedWrapper.getValues();
            int[] types = preparedWrapper.getTypes();
            if (showSql){
                System.out.println(preparedWrapper);
            }
            ps = connection.prepareStatement(preparedSql);
            for (int i = 0; i < values.length; i++) {
                setValue(ps,i+1,types[i],values[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException("execute sql failed, preparedSql :"+preparedWrapper.toString(),e);
        } finally {
            try{
                if (ps!=null){
                    ps.close();
                }
            }catch (SQLException e){
                throw new EasyJpaException(e);
            }
        }

    }

    private static void setValue(PreparedStatement ps, int paramIndex, int sqlType, Object inValue) throws SQLException {

        if (sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR ) {
            ps.setString(paramIndex, String.valueOf(inValue));
        }else if (sqlType == Types.NVARCHAR || sqlType == Types.LONGNVARCHAR) {
            ps.setNString(paramIndex, String.valueOf(inValue));
        }else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) {
            ps.setObject(paramIndex, inValue, sqlType);
        }else if (sqlType == Types.BOOLEAN) {
            if (inValue instanceof Boolean) {
                ps.setBoolean(paramIndex, (Boolean) inValue);
            }
            else {
                ps.setObject(paramIndex, inValue, Types.BOOLEAN);
            }
        }else if (sqlType == Types.DATE) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof Date) {
                    ps.setDate(paramIndex, (Date) inValue);
                }
                else {
                    ps.setDate(paramIndex, new Date(((java.util.Date) inValue).getTime()));
                }
            }
            else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setDate(paramIndex, new Date(cal.getTime().getTime()), cal);
            }
            else {
                ps.setObject(paramIndex, inValue, Types.DATE);
            }
        }else if (sqlType == Types.TIME) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof Time) {
                    ps.setTime(paramIndex, (Time) inValue);
                }
                else {
                    ps.setTime(paramIndex, new Time(((java.util.Date) inValue).getTime()));
                }
            }
            else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTime(paramIndex, new Time(cal.getTime().getTime()), cal);
            }
            else {
                ps.setObject(paramIndex, inValue, Types.TIME);
            }
        }
        else if (sqlType == Types.TIMESTAMP) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof Timestamp) {
                    ps.setTimestamp(paramIndex, (Timestamp) inValue);
                }
                else {
                    ps.setTimestamp(paramIndex, new Timestamp(((java.util.Date) inValue).getTime()));
                }
            }
            else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTimestamp(paramIndex, new Timestamp(cal.getTime().getTime()), cal);
            }
            else {
                ps.setObject(paramIndex, inValue, Types.TIMESTAMP);
            }
        }else {
            ps.setObject(paramIndex, inValue, sqlType);
        }
    }
}
