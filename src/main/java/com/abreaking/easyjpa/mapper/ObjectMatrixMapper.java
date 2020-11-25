package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.exception.EntityObjectNeedsException;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * 实体对象的映射关系描述
 * @author liwei_paas
 * @date 2020/7/13
 */
public class ObjectMatrixMapper implements MatrixMapper {

    Object entity;

    ClassMatrixMapper classMatrixMapper;

    public ObjectMatrixMapper(Object entity){
        this.classMatrixMapper = ClassMatrixMapper.map(entity.getClass());
        this.entity = entity;
    }

    @Override
    public String tableName() {
        return classMatrixMapper.tableName();
    }

    @Override
    public ColumnMatrix mapId() {
        return matrixFields(Collections.singletonList(classMatrixMapper.getId()), classMatrixMapper.mapId());
    }

    @Override
    public ColumnMatrix mapPks() {
        return matrixFields(classMatrixMapper.getPks(), classMatrixMapper.mapPks());
    }

    @Override
    public ColumnMatrix matrix() {
        return matrixFields(classMatrixMapper.getMappingFields(), classMatrixMapper.matrix());
    }

    private ColumnMatrix matrixFields(List<Field> fields,ColumnMatrix matrix){
        String[] columns = matrix.columns();
        ColumnMatrix newMatrix = new AxisColumnMatrix(columns.length);
        for (int i = 0; i < columns.length; i++) {
            Field field = fields.get(i);
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if (value!=null){
                    newMatrix.put(matrix.getColumn(i),matrix.getType(i),value);
                }
            } catch (IllegalAccessException e) {
                //这里异常可以忽略，因为前面已经判断了，这些字段都是从实体类中有getter方法的字段
            }
        }
        return newMatrix;
    }
}
