package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.exception.EasyJpaException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 *
 * @author liwei
 * @date 2021/3/19
 */
public class ConnectionHolder {

    private Connection connection;

    private DatabaseMetaData metaData;

    private String jdbcUrl ;
    private String jdbcUserName ;
    private String jdbcDriverName ;
    private String dialect; //方言

    private static final ThreadLocal<ConnectionHolder> HOLDER_CONNECTION_LOCAL = new ThreadLocal();

    private ConnectionHolder(DataSource dataSource) throws SQLException {
        this(dataSource.getConnection());
    }

    private ConnectionHolder(Connection connection) throws SQLException {
        this.connection = connection;
        this.metaData = connection.getMetaData();
        this.jdbcUrl = metaData.getURL();
        this.jdbcUserName = metaData.getUserName();
        this.jdbcDriverName = metaData.getDriverName();
        this.dialect = getDialectByJdbcUrl(jdbcUrl);
    }

    public static void setLocalConnection(Connection connection){
        try {
            ConnectionHolder holder = new ConnectionHolder(connection);
            HOLDER_CONNECTION_LOCAL.set(holder);
        } catch (SQLException e) {
            throw new EasyJpaException(e);
        }
    }

    public static ConnectionHolder getLocalConnection(){
        return HOLDER_CONNECTION_LOCAL.get();
    }

    public static void removeLocalConnection(){
        HOLDER_CONNECTION_LOCAL.remove();
    }

    public Connection getConnection() {
        return connection;
    }

    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUserName() {
        return jdbcUserName;
    }

    public String getJdbcDriverName() {
        return jdbcDriverName;
    }

    public String getDialect() {
        return dialect;
    }


    /**
     * 通过url来判断数据库的方言
     * @param jdbcUrl jdbcUrl
     */
    public static String getDialectByJdbcUrl(String jdbcUrl){
        if (jdbcUrl.startsWith("jdbc:mysql")){
            return "mysql";
        }
        if (jdbcUrl.startsWith("jdbc:oracle")){
            return "oracle";
        }
        if (jdbcUrl.startsWith("jdbc:postgresql")){
            return "postgresql";
        }
        if (jdbcUrl.startsWith("jdbc:db2")){
            return "db2";
        }
        if (jdbcUrl.indexOf("mysql") != -1){
            return "mysql";
        }
        if (jdbcUrl.indexOf("oracle") != -1){
            return "oracle";
        }
        if (jdbcUrl.indexOf("postgresql") != -1){
            return "postgresql";
        }
        if (jdbcUrl.indexOf("db2") != -1){
            return "db2";
        }
        return "mysql"; //默认mysql
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(jdbcUrl);
        builder.append(".").append(jdbcUserName).append(".").append(jdbcDriverName);
        return builder.toString();
    }


}
