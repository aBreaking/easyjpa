package com.abreaking.easyjpa.exception;

import java.util.Arrays;

/**
 * sql执行异常
 * @author liwei_paas
 * @date 2020/11/26
 */
public class EasyJpaSqlExecutionException extends EasyJpaException {

    public EasyJpaSqlExecutionException(String prepareSql,Object[] values,Throwable cause){
        this("executing sql failed. the prepared sql : ["+prepareSql+"], values : "+Arrays.toString(values),cause);
    }

    public EasyJpaSqlExecutionException(String message) {
        super(message);
    }

    public EasyJpaSqlExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyJpaSqlExecutionException(Throwable cause) {
        super(cause);
    }

}
