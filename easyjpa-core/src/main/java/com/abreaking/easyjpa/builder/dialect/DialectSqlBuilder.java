package com.abreaking.easyjpa.builder.dialect;

import com.abreaking.easyjpa.executor.ConnectionHolder;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author liwei_paas
 * @date 2021/1/4
 */
public class DialectSqlBuilder {

    /**
     * 使用享元模式，所有的dialect 对应的sqlbuilder都是单例
     */
    private static final Map<String,DialectSqlBuilder> instanceMap = new HashMap<>();


    public static DialectSqlBuilder getDialectSqlBuilder(String dialect){
        dialect = dialect.toLowerCase();
        if (!instanceMap.containsKey(dialect)){
            synchronized (instanceMap){
                if (!instanceMap.containsKey(dialect)){
                    switch (dialect){
                        case "mysql" : instanceMap.put(dialect,new MysqlDialectSqlBuilder());break;
                        case "oracle" : instanceMap.put(dialect,new OracleDialectSqlBuilder());break;
                        default : return instanceMap.getOrDefault("mysql",new MysqlDialectSqlBuilder()); //默认也是mysql的sqlbuilder，但是key不缓存，免得instanceMap数据超大
                    }
                }
            }
        }
        return instanceMap.get(dialect);
    }


    public static DialectSqlBuilder getDefaultDialectSqlBuilder(){
        ConnectionHolder localConnection = ConnectionHolder.getLocalConnection();
        String config = localConnection==null?"mysql":localConnection.getDialect();
        return getDialectSqlBuilder(config);
    }


    /**
     * 分页
     * @param sqlBuilder
     * @param columnMatrix
     * @param pageStartIndex 分页开始的行号（从O开始）
     * @param pageSize 分页大小
     */
    public void visitPage(StringBuilder sqlBuilder,ColumnMatrix columnMatrix, int pageStartIndex, int pageSize){};

}
