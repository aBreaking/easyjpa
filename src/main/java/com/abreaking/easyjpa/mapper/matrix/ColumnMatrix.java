package com.abreaking.easyjpa.mapper.matrix;

/**
 * 对ColumnMatrix的增强，增加一些方法
 * @author liwei_paas 
 * @date 2019/8/28
 */
public interface ColumnMatrix extends Matrix {

    /**
     * 判断是否空
     * @return
     */
    boolean isEmpty();

    /**
     * 添加三维属性
     * @param column
     * @param type
     * @param value
     */
    void put(String column, int type, Object value);

    /**
     * 删除column的记录
     * @param column
     */
    void remove(String column);

    /**
     *
     * @param column
     * @return -1 when column not exist , >=0 when column exist
     */
    int indexOf(String column);

    int getType(int index);

    Object getValue(int index);

    String getColumn(int index);
}
