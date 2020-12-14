package com.abreaking.easyjpa.mapper.matrix;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持对column索引的matrix，这样跟据column找相关的值更快了
 * 这里使用装饰者模式，而不是直接去实现接口，免去多余操作
 * @author liwei_paas
 * @date 2020/11/13
 */
@Deprecated
public class IndexColumnMatrix implements ColumnMatrix{

    ColumnMatrix columnMatrix;

    private Map<String,Integer> map = new HashMap<>();

    public IndexColumnMatrix(ColumnMatrix columnMatrix) {
        this.columnMatrix = columnMatrix;
        String[] columns = columnMatrix.columns();
        for (int i = 0; i < columns.length; i++) {
            map.put(columns[i],i);
        }
    }

    public boolean hasColumn(String column){
        return map.containsKey(column);
    }

    public int getType(String column){
        int i = indexOf(column);
        return getType(i);
    }

    public Object getValue(String column){
        int i = indexOf(column);
        return getValue(i);
    }

    @Override
    public boolean isEmpty() {
        return columnMatrix.isEmpty();
    }

    @Override
    public void put(String column, int type, Object value) {
        columnMatrix.put(column,type,value);
    }

    @Override
    public void remove(int i) {
        columnMatrix.remove(i);
    }

    @Override
    public int indexOf(String column) {
        return map.get(column);
    }

    @Override
    public int getType(int index) {
        return columnMatrix.getType(index);
    }

    @Override
    public Object getValue(int index) {
        return columnMatrix.getValue(index);
    }

    @Override
    public String getColumn(int index) {
        return columnMatrix.getColumn(index);
    }

    @Override
    public String[] columns() {
        return columnMatrix.columns();
    }

    @Override
    public int[] types() {
        return columnMatrix.types();
    }

    @Override
    public Object[] values() {
        return columnMatrix.values();
    }
}
