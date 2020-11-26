package com.abreaking.easyjpa.mapper.annotation;

import java.lang.annotation.*;
import java.sql.Types;

/**
 * 列
 * 实体属性 与 表的列 之间的映射
 * @author liwei_paas 
 * @date 2019/8/28
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * 列名。缺省就是将属性名（驼峰形式xxxYyyZzz）转为列名（xxx_yyy_zzz）
     * @return
     */
    String value() default "";

    /**
     * 列类型。缺省通过框架里得规则将java的类型转为 表相应的类型
     * @return
     */
    int type() default Types.NULL;
}
