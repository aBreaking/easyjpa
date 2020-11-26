package com.abreaking.easyjpa.dao;

/**
 * sql执行异常
 * @author liwei_paas
 * @date 2020/11/26
 */
public class EasyJpaSqlExecutionException extends RuntimeException{


    public EasyJpaSqlExecutionException(String message) {
        super(message);
    }

    public EasyJpaSqlExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyJpaSqlExecutionException(Throwable cause) {
        super(cause);
    }

    public EasyJpaSqlExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
