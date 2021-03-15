package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.mapper.matrix.MatrixFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 将实体对象映射成matrix
 * @author liwei_paas
 * @date 2021/2/7
 */
public class EntityMatrixMapper<T> implements MatrixMapper {

    T entity;

    public EntityMatrixMapper(T entity){
        this.entity = entity;
    }

    @Override
    public Matrix matrix() {
        ClassMapper classMapper = ClassMapper.map(entity.getClass());
        ColumnMatrix matrix = MatrixFactory.createColumnMatrix();
        for (FieldMapper fieldMapper : classMapper.allMappableFields()){
            try {
                Method getterMethod = fieldMapper.getGetterMethod();
                Object value = getterMethod.invoke(entity);
                if (value!=null){
                    String columnName = fieldMapper.getColumnName();
                    int columnType = fieldMapper.getColumnType();
                    matrix.put(columnName,columnType,value);
                }
            } catch (IllegalAccessException|InvocationTargetException e) {
            }
        }
        return matrix;
    }
}
