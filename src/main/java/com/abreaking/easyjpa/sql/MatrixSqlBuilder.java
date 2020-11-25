package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;

import java.util.TreeMap;

/**
 *
 * @author liwei_paas
 * @date 2020/11/13
 */
public class MatrixSqlBuilder {

    private Class obj;
    private ColumnMatrix condition;

    TreeMap<String,String> treeMap = new TreeMap<>();

    public MatrixSqlBuilder(){
        String[] columns = condition.columns();
        for (int i = 0; i < columns.length; i++) {
            treeMap.put(columns[i]," = ?");
        }
    }


    public void doPut(String column,String value){

    }

}
