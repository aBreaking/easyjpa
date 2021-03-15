package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.exception.EntityObjectNeedsException;
import com.abreaking.easyjpa.mapper.ClassMapper;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.mapper.RowMapper;
import com.abreaking.easyjpa.util.SqlUtil;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 对实体的映射，接收一个实体对象，配合SqlBuilder 可将其处理成可执行的预sql
 * @author liwei_paas
 * @date 2020/12/3
 */
public class ClassRowMapper<T> implements RowMapper {

    // 实体的class对象
    protected Class obj;

    // 实体的映射信息
    protected ClassMapper classMapper;


    public ClassRowMapper(Class<T> obj){
        this.obj = obj;
        this.classMapper = ClassMapper.map(obj);
    }


    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 从结果集中映射出对象，如果是单值查询的话，考虑是否直接使用原对象)
        Object instance;
        try {
            instance = obj.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EntityObjectNeedsException(obj+"必须是实体的类，并且至少有一个空的构造方法");
        }
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            FieldMapper fieldMapper = classMapper.mapField(columnName);
            if (fieldMapper ==null){
                continue;
            }
            try {
                Field field = fieldMapper.getField();
                field.setAccessible(true);
                Object o = SqlUtil.getSoftResultSetValue(rs, i, field.getType());
                field.set(instance,o);
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassRowMapper<?> that = (ClassRowMapper<?>) o;
        return Objects.equals(obj, that.obj);
    }

    @Override
    public int hashCode() {

        return Objects.hash(obj);
    }
}
