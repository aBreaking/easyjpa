package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Entry;
import com.abreaking.easyjpa.exception.EasyJpaSqlExecutionException;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.ClassRowMapper;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.MatrixSqlBuilder;
import com.abreaking.easyjpa.sql.SelectSqlBuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 通用增删改查的模板
 * @author liwei_paas
 * @date 2020/12/13
 */
public class CurdTemplate<T> {

    protected SqlExecutor sqlExecutor;

    public CurdTemplate(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public List<T> select(AbstractEasyJpa jpa) {
        return doSelect(jpa,new SelectSqlBuilder());
    }

    public void update(AbstractEasyJpa t) {
        doUpdate(t,new MatrixSqlBuilder() {
            @Override
            protected void doVisit(AbstractEasyJpa easyJpa) {
                add("UPDATE ",easyJpa.getTableName());
                add(" SET ");
                Collection<Entry> entryList = easyJpa.entry();
                for (Entry entry : entryList){
                    add(entry.getColumnName());
                    add("=?,",entry.getValue());
                }
                add(" WHERE ");
                removeLastSeparator(sqlBuilder,",");
                Entry id = easyJpa.getIdEntry();
                add(id.getColumnName());
                add("=?",id.getValue());
            }
        });
    }

    public void insert(AbstractEasyJpa t) {
        doUpdate(t, new MatrixSqlBuilder() {
            @Override
            protected void doVisit(AbstractEasyJpa easyJpa) {
                add("INSERT INTO ",easyJpa.getTableName());
                add("(");
                Collection<Entry> entryList = easyJpa.entry();
                for (Entry  entry : entryList){
                    add(entry.getColumnName());
                    add(",");
                }
                removeLastSeparator(sqlBuilder,",");
                add(") VALUES(");
                for (Entry  entry : entryList){
                    add("?",entry.getValue());
                    add(",");
                }
                removeLastSeparator(sqlBuilder,",");
                add(")");
            }
        });
    }

    public void delete(AbstractEasyJpa t){
        doUpdate(t, new MatrixSqlBuilder() {
            @Override
            protected void doVisit(AbstractEasyJpa easyJpa) {
                add("DELETE FROM ",easyJpa.getTableName());
                Entry idEntry = easyJpa.getIdEntry();
                add(" WHERE ");
                add(idEntry.getColumnName());
                add("=?",idEntry.getValue());
            }
        });
    }

    public List<T> doSelect(AbstractEasyJpa jpa,SelectSqlBuilder sqlBuilder) {
        jpa.accept(sqlBuilder);
        Matrix matrix = sqlBuilder.toMatrix();
        String toPreparedSql = sqlBuilder.toPrepareSql();

        // 实体的rowMapper
        RowMapper rowMapper = new ClassRowMapper(jpa.getObj());
        try {
            return sqlExecutor.query(toPreparedSql,matrix.values(),matrix.types(),rowMapper);
        }catch (SQLException e){
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    protected void doUpdate(AbstractEasyJpa jpa,MatrixSqlBuilder sqlBuilder) {
        jpa.accept(sqlBuilder);
        String sql = sqlBuilder.toPrepareSql();
        Matrix matrix = sqlBuilder.toMatrix();
        System.out.println(sql);
        System.out.println(matrix);
        try {
            sqlExecutor.update(sql,matrix.values(),matrix.types());
        } catch (SQLException e) {
            throw new EasyJpaSqlExecutionException(e);
        }
    }

    private void removeLastSeparator(StringBuilder sqlBuilder,String separator){
        if (sqlBuilder.lastIndexOf(separator)!=-1){
            int index = sqlBuilder.lastIndexOf(separator);
            sqlBuilder.replace(index,index+1,"");
        }
    }

}
