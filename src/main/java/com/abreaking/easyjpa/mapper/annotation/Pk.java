package com.abreaking.easyjpa.mapper.annotation;

import java.lang.annotation.*;
import java.sql.Types;

/**
 * pk，即primary key，叫做业务主键。
 * 与自然主键{@link Id} 区别是，它可以用在多个字段上面。 一般来讲，业务主键都是手动分配的。
 * @author liwei_paas
 * @date 2019/7/4
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pk {
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
