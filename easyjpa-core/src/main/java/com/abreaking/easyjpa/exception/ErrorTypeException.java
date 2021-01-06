package com.abreaking.easyjpa.exception;


/**
 * 类型错误的异常
 * @author liwei_paas
 * @date 2021/1/6
 */
public class ErrorTypeException extends EasyJpaException {

    public ErrorTypeException() {
    }

    public ErrorTypeException(String message) {
        super(message);
    }

    public ErrorTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorTypeException(Throwable cause) {
        super(cause);
    }

    public ErrorTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
