package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.annotation.Column;
import com.abreaking.easyjpa.mapper.annotation.Id;
import com.abreaking.easyjpa.mapper.annotation.Pk;
import com.abreaking.easyjpa.util.SqlUtil;
import com.abreaking.easyjpa.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author liwei_paas
 * @date 2020/12/8
 */
public class FieldMapper {

    private Method getterMethod;
    private Field field;
    private String columnName;
    private int columnType;
    private String fieldAnnotation = "column";

    public static final String FIELD_ANNOTATION_ID = "Id";
    public static final String FIELD_ANNOTATION_PK = "Pk";

    public FieldMapper(Field field){
        this.field = field;
        field.setAccessible(true);
        //字段上的注解处理
        if (field.isAnnotationPresent(Id.class)) {
            Id id = field.getAnnotation(Id.class);
            columnName = id.value();
            columnType = id.type();
            fieldAnnotation = FIELD_ANNOTATION_ID;
        }else if (field.isAnnotationPresent(Pk.class)) {
            Pk pk = field.getAnnotation(Pk.class);
            columnName = pk.value();
            columnType = pk.type();
            fieldAnnotation = FIELD_ANNOTATION_PK;
        }else if (field.isAnnotationPresent(Column.class)){
            Column column = field.getAnnotation(Column.class);
            columnName = column.value();
            columnType = column.type();
        }
        if (StringUtils.isEmpty(columnName)){
            columnName = StringUtils.underscoreName(field.getName());
        }
        if (columnType == 0){
            columnType = SqlUtil.getSqlType(field.getType());
        }
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public void setGetterMethod(Method getterMethod) {
        this.getterMethod = getterMethod;
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

}
