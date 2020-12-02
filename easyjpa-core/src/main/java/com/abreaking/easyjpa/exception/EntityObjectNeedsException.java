package com.abreaking.easyjpa.exception;

/**
 * 必须是实体对象
 * @author liwei_paas
 * @date 2020/11/3
 */
public class EntityObjectNeedsException extends EasyJpaException {
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

}
