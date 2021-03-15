package com.abreaking.easyjpa.mapper.matrix;

/**
 *
 * @author liwei_paas
 * @date 2020/12/25
 */
@Deprecated
public class MatrixFactory {

    public static ColumnMatrix createColumnMatrix(int size){
        return new AxisColumnMatrix(size);
    }
    public static ColumnMatrix createColumnMatrix(){
        return new AxisColumnMatrix();
    }
}
