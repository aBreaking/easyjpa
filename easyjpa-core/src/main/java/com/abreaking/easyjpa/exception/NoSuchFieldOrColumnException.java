package com.abreaking.easyjpa.exception;

/**
 * 没有这样的 字段 或 列名抛出的异常
 * @author liwei_paas
 * @date 2020/12/25
 */
public class NoSuchFieldOrColumnException extends EasyJpaException{

    public NoSuchFieldOrColumnException(Class obj, String fcName) {
        super(obj.getName()+"类或对应的表不包含"+fcName+"这样的字段名或列名");
    }

    public NoSuchFieldOrColumnException(String message) {
        super(message);
    }

    public NoSuchFieldOrColumnException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchFieldOrColumnException(Throwable cause) {
        super(cause);
    }

    public NoSuchFieldOrColumnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
