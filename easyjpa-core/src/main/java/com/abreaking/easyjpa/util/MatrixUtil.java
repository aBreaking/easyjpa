package com.abreaking.easyjpa.util;

import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;

/**
 * 针对Matrix的一些工具
 * @author liwei_paas
 * @date 2020/12/2
 */
public class MatrixUtil {

    public static ColumnMatrix addValue(Matrix matrix, Object...values){
        String[] columns = matrix.columns();
        int[] types = matrix.types();
        ColumnMatrix columnMatrix = new AxisColumnMatrix(columns.length);
        for (int i = 0; i < columns.length && i<values.length; i++) {
            columnMatrix.put(columns[i],types[i],values[i]);
        }
        return columnMatrix;
    }
}
