package com.abreaking.easyjpa.exception;

/**
 * 通用的exception
 * @author liwei_paas
 * @date 2020/12/2
 */
public class EasyJpaException extends RuntimeException{
    public EasyJpaException() {
    }

    public EasyJpaException(String message) {
        super(message);
    }

    public EasyJpaException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyJpaException(Throwable cause) {
        super(cause);
    }

    public EasyJpaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
