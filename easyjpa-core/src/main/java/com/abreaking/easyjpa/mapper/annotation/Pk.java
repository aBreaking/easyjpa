package com.abreaking.easyjpa.mapper.annotation;

import java.lang.annotation.*;
import java.sql.Types;

/**
 * pk，即primary key，叫做业务主键。
 * 与自然主键{@link javax.persistence.Id} 区别是，它可以用在多个字段上面。 一般来讲，业务主键都是手动分配的。
 * @author liwei_paas
 * @date 2019/7/4
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pk {
}
