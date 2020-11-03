package com.abreaking.easyjpa.mapper.exception;

/**
 * 必须是实体对象
 * @author liwei_paas
 * @date 2020/11/3
 */
public class EntityObjectNeedsException extends RuntimeException{
    public EntityObjectNeedsException() {
    }

    public EntityObjectNeedsException(String message) {
        super(message);
    }

    public EntityObjectNeedsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityObjectNeedsException(Throwable cause) {
        super(cause);
    }

    public EntityObjectNeedsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
