package com.abreaking.easyjpa.exception;

/**
 * 没有指定主键的异常
 * @author liwei_paas
 * @date 2019/11/22
 */
public class NoIdOrPkSpecifiedException extends EasyJpaException {

    public NoIdOrPkSpecifiedException(Class obj) {
        super(obj.getName()+" has no primary key! STRONGLY RECOMMEND: every table should has primary key,You can use the @Id or @Pk annotation to identify the primary key on your entity class");
    }

    public NoIdOrPkSpecifiedException() {
    }

    public NoIdOrPkSpecifiedException(String message) {
        super(message);
    }

    public NoIdOrPkSpecifiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoIdOrPkSpecifiedException(Throwable cause) {
        super(cause);
    }

}
