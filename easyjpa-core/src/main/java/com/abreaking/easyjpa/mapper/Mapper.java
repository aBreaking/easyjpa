package com.abreaking.easyjpa.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author liwei_paas
 * @date 2020/12/8
 */
public class Mapper {
    private Method getterMethod;
    private Field field;
    private String columnName;
    private int columnType;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public void setGetterMethod(Method getterMethod) {
        this.getterMethod = getterMethod;
    }
}
