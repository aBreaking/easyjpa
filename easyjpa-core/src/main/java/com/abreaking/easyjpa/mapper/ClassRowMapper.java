package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.exception.EntityObjectNeedsException;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 对结果集的映射
 * @author liwei_paas
 * @date 2020/11/3
 */
public class ClassRowMapper extends BaseRowMapper{

    private ClassMapper classMapper;

    public ClassRowMapper(Class obj){
        super(obj);
        this.classMapper = ClassMapper.map(obj);
    }
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 从结果集中映射出对象，如果是单值查询的话，考虑是否直接使用原对象)
        Object instance = null;
        try {
            instance = obj.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            //如果不是实体对象，那么就考虑返回数组

            throw new EntityObjectNeedsException(obj+"必须是实体的类，并且至少有一个空的构造方法");
        }
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            FieldMapper fieldMapper = classMapper.getMapper(columnName);
            if (fieldMapper ==null){
                continue;
            }
            try {
                Field field = fieldMapper.getField();
                field.setAccessible(true);
                Object o = getResultSetValue(rs, i, field.getType());
                field.set(instance,o);
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return instance;
    }

}
