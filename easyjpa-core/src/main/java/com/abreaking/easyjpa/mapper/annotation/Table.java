package com.abreaking.easyjpa.mapper.annotation;

import java.lang.annotation.*;

/**
 * 表名。
 * 固定的表名，用于 实体-表 的映射
 * @author liwei_paas 
 * @date 2019/8/28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

    /**
     * 表名。不指定的话默认根据类名(XxxYyyZzz)获取表名(xxx_yyy_zzz)
     * @return
     */
    String value() default "";
}
