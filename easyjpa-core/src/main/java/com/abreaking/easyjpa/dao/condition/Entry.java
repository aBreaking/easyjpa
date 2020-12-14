package com.abreaking.easyjpa.dao.condition;

import java.util.Objects;

/**
 * 将实体对象的作为条件 映射
 * @author liwei_paas
 * @date 2020/12/14
 */
public class Entry {
    private String columnName;
    private String operator;
    private Object value;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return Objects.equals(columnName, entry.columnName) &&
                Objects.equals(operator, entry.operator) &&
                Objects.equals(value, entry.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(columnName, operator, value);
    }
}
