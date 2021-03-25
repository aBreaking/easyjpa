package com.abreaking.easyjpa.builder.prepare;


import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.util.SqlUtil;

import java.util.Arrays;

/**
 * 预处理的sql及其参数封装
 * @author liwei
 * @date 2021/2/23
 */
public class PreparedWrapper {

    private String preparedSql; //预处理的sql语句

    private Object[] values; //sql里的参数值

    private int[] types; //参数类型

    public PreparedWrapper() {
    }

    public PreparedWrapper(String preparedSql, Object[] args, int[] types) {
        this.preparedSql = preparedSql;
        this.values = args;
        this.types = types;
    }

    public PreparedWrapper(String preparedSql, Object...args) {
        this.preparedSql = preparedSql;
        this.values = args;
        this.types = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = SqlUtil.getSqlTypeByValue(args[i]);
        }
    }

    public PreparedWrapper(String preparedSql, Matrix matrix) {
        this.preparedSql = preparedSql;
        this.values = matrix.values();
        this.types = matrix.types();
    }

    public String getPreparedSql() {
        return preparedSql;
    }

    public void setPreparedSql(String preparedSql) {
        this.preparedSql = preparedSql;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public int[] getTypes() {
        return types;
    }

    public void setTypes(int[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "PreparedSql{" +
                "preparedSql='" + preparedSql + '\'' +
                ", values=" + Arrays.toString(values) +
                ", types=" + Arrays.toString(types) +
                '}';
    }
}
