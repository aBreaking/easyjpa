package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Entry;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.ClassRowMapper;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.sql.MatrixSqlBuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 通用增删改查的模板
 * @author liwei_paas
 * @date 2020/12/13
 */
public class CurdTemplate<T> {

    SqlExecutor sqlExecutor;

    public CurdTemplate(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public List<T> select(Object t) throws SQLException {
        // easy jpa 对实体进行第一次的处理, 获取实体的映射信息
        AbstractEasyJpa<Object> jpa = new EasyJpa<>(t);

        // sqlBuilder 使用jpa里的方法，开始拼装sql，返回matrix
        MatrixSqlBuilder sqlBuilder = new MatrixSqlBuilder() {
            @Override
            protected void doVisit(AbstractEasyJpa easyJpa) {
                String tableName = easyJpa.getTableName();
                Collection<Entry> entryList = easyJpa.entry();
                add("SELECT * FROM ",tableName);
                if (!entryList.isEmpty()){
                    add(" WHERE 1=1 ");
                    for (Entry entry : entryList){
                        add(" and ");
                        add(entry.getColumnName());
                        add(entry.getOperator());
                        add("?",entry.getValue());
                    }
                }
            }
        };
        jpa.accept(sqlBuilder);
        Matrix matrix = sqlBuilder.toMatrix();
        String toPreparedSql = sqlBuilder.toPrepareSql();

        // 实体的rowMapper
        RowMapper rowMapper = new ClassRowMapper(t.getClass());

        return sqlExecutor.query(toPreparedSql,matrix.values(),matrix.types(),rowMapper);
    }

    public void update(Object t) throws SQLException {
        doUpdate(t,new MatrixSqlBuilder() {
            @Override
            protected void doVisit(AbstractEasyJpa easyJpa) {
                add("UPDATE ",easyJpa.getTableName());
                add(" SET ");
                Collection<Entry> entryList = easyJpa.entry();
                for (Entry entry : entryList){
                    add(entry.getColumnName());
                    add(",");
                }
                add(" WHERE ");
                if (sqlBuilder.lastIndexOf(",")!=-1){
                    int index = sqlBuilder.lastIndexOf(",");
                    sqlBuilder.replace(index,index+1,"");
                }
                Entry id = easyJpa.getIdEntry();
                add(id.getColumnName());
                add("=?",id.getValue());
            }
        });
    }

    public void insert(Object t) throws SQLException {
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

    public void delete(Object t) throws SQLException{
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

    private void doUpdate(Object t,MatrixSqlBuilder sqlBuilder) throws SQLException {
        AbstractEasyJpa<Object> jpa = new EasyJpa<>(t);
        jpa.accept(sqlBuilder);
        String sql = sqlBuilder.toPrepareSql();
        Matrix matrix = sqlBuilder.toMatrix();
        System.out.println(sql);
        System.out.println(matrix);
        sqlExecutor.update(sql,matrix.values(),matrix.types());
    }

    private void removeLastSeparator(StringBuilder sqlBuilder,String seperator){
        if (sqlBuilder.lastIndexOf(",")!=-1){
            int index = sqlBuilder.lastIndexOf(",");
            sqlBuilder.replace(index,index+1,"");
        }
    }

}
