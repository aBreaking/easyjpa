package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;

/**
 * 实体的映射关系描述
 * @author liwei_paas
 * @date 2020/11/3
 */
public interface MatrixMapper {

    /**
     * 映射后的表名
     * @return
     */
    String tableName();

    /**
     * 字段->列的映射关系，使用matrix描述，下同
     * @return
     */
    ColumnMatrix matrix();

    /**
     * 主键的映射关系
     * @return
     */
    ColumnMatrix mapId();

    /**
     * 业务主键的映射关系
     * @return
     */
    ColumnMatrix mapPks();

}
