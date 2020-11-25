package com.abreaking.easyjpa.sql;

/**
 *
 * @author liwei_paas
 * @date 2020/11/3
 */
public interface CriteriaSqlBuilder extends SqlBuilder{

    SqlBuilder like(String fieldName,String likeValue);

    SqlBuilder gt(String fieldName,Object value);

    SqlBuilder lt(String fieldName,Object value);
}
