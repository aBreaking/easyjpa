package com.abreaking.easyjpa.mapper.exception;

/**
 * 没有指定主键的异常
 * @author liwei_paas
 * @date 2019/11/22
 */
public class NoIdOrPkSpecifiedException extends RuntimeException{
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

    public NoIdOrPkSpecifiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
