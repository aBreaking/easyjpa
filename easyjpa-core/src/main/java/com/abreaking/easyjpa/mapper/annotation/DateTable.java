package com.abreaking.easyjpa.mapper.annotation;

import java.lang.annotation.*;

/**
 * 针对日期表，制定的注解。
 * 作用在实体类上，会自动将dateFormat的日期格式解析成实际日期，然后再加上表名的前后缀
 *
 * 比如：DateTable(tablPrefix="hello_",dateFormat="yyyymmdd",tableSuffix="_world")
 * 那么会根据当前时间，自动将表名处理成： hello_20210329_world
 *
 * @author liwei
 * @date 2021/3/29
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateTable {

    /**
     * 表名前缀
     * @return
     */
    String tablePrefix();


    /**
     *  时间的格式
     * @return
     */
    String dateFormat() default "";

    /**
     * 表名后缀
     * @author liwei
     * @date 2021/3/29
     */
    String tableSuffix() default "";

}
