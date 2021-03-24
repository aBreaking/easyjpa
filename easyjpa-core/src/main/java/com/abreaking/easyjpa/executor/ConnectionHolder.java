package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.exception.EasyJpaException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author liwei
 * @date 2021/3/19
 */
public class ConnectionHolder {

    private static final ThreadLocal<ConnectionWrapper> HOLDER_CONNECTION_LOCAL = new ThreadLocal();

    public static void setLocalConnection(Connection connection){
        try {
            ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection);
            HOLDER_CONNECTION_LOCAL.set(connectionWrapper);
        } catch (SQLException e) {
            throw new EasyJpaException(e);
        }
    }

    public static ConnectionWrapper getLocalConnection(){
        return HOLDER_CONNECTION_LOCAL.get();
    }

    public static void removeLocalConnection(){
        HOLDER_CONNECTION_LOCAL.remove();
    }

}
