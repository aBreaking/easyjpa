package com.abreaking.easyjpa.mapper.annotation;

import java.lang.annotation.*;
import java.sql.Types;

/**
 * 自然主键。
 * 与{@link Pk}不同的是，它只能是用在一个字段上面。此外，它的值一般都是自动分配的，比如主键自增、uuid等
 * @author liwei_paas 
 * @date 2019/8/28
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
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

    /**
     * 主键分配的策略
     * @return
     */
    int strategy() default 0;
}
