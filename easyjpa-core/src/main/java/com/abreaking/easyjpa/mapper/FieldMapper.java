package com.abreaking.easyjpa.mapper;

import javax.persistence.Column;
import javax.persistence.Id;
import com.abreaking.easyjpa.mapper.annotation.Pk;
import com.abreaking.easyjpa.util.SqlUtil;
import com.abreaking.easyjpa.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 描述 字段->列 之间的映射关系
 * @author liwei_paas
 * @date 2020/12/8
 */
public class FieldMapper {
    private Field field;

    private String filedName;
    private String columnName;
    private int columnType;
    private Method getterMethod;
    private String fieldAnnotation = "Column";

    public static final String FIELD_ANNOTATION_ID = "Id";
    public static final String FIELD_ANNOTATION_PK = "Pk";

    public FieldMapper(Field field,Method method){
        this.field = field;
        field.setAccessible(true);
        // 特殊字段记录
        if (field.isAnnotationPresent(Id.class)) {
            fieldAnnotation = FIELD_ANNOTATION_ID;
        }else if (field.isAnnotationPresent(Pk.class)) {
            fieldAnnotation = FIELD_ANNOTATION_PK;
        }

        this.columnName = field.isAnnotationPresent(Column.class)?
                field.getAnnotation(Column.class).name():
                StringUtils.underscoreName(field.getName());
        this.columnType = SqlUtil.getSoftSqlType(field.getType());
        this.getterMethod = method;
        this.filedName = field.getName();
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public Field getField() {
        return field;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public String getFieldAnnotation() {
        return fieldAnnotation;
    }

    public String getFiledName() {
        return filedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldMapper that = (FieldMapper) o;
        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
