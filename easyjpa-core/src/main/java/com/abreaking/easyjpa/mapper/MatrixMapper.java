package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.matrix.Matrix;

/**
 * 实体的映射关系描述
 * @author liwei_paas
 * @date 2020/11/3
 */
public interface MatrixMapper {

    /**
     * 字段->列的映射关系，使用matrix描述，下同
     * @return
     */
    Matrix matrix();


}
