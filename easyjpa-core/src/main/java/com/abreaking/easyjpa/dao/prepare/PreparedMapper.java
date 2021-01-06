package com.abreaking.easyjpa.dao.prepare;


/**
 *
 * @author liwei_paas
 * @date 2021/1/6
 */
public class PreparedMapper {

    private String prepareSql;
    private Object[] args;

    public PreparedMapper(String prepareSql) {
        this.prepareSql = prepareSql;
    }

    public PreparedMapper(String prepareSql,Object...args) {
        this.prepareSql = prepareSql;
        this.args = args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getPrepareSql() {
        return prepareSql;
    }

    public Object[] getArgs() {
        return args;
    }
}
