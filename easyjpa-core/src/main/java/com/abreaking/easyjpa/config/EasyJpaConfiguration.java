package com.abreaking.easyjpa.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Properties;

/**
 * 整个系统全局配置，它主要是维护一个配置枚举类Configuration
 * 它的配置可以从jdbc连接信息而来，也可以从配置文件中而来，当然所有配置，都会有默认配置的
 * 顺序为：配置文件 > jdbc连接信息 > 默认配置
 * @author liwei_paas
 * @date 2020/12/31
 */
public class EasyJpaConfiguration {
    // 默认配置文件名，当然可以不需要
    public static final String DEFAULT_PROPERTIES_FILE_NAME = "easyjpa.properties";
    // 配置的前缀名 ,在配置文件中的配置名一般都是 easyjpa.xxx 这种格式
    public static final String PROPERTIES_PREFIX = "easyjpa";

    static {
        initConfigurationWithProperties();
    }

    /**
     * 根据配置文件初始化配置
     */
    public static void initConfigurationWithProperties(){
        try {
            ClassLoader classLoader = EasyJpaConfiguration.class.getClassLoader();
            URL url = classLoader.getResource(DEFAULT_PROPERTIES_FILE_NAME);
            if (url==null){
                return;
            }
            File file = new File(url.getFile());
            if (!file.exists()){
                return;
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            for (Configuration configuration : Configuration.values()){
                String key = PROPERTIES_PREFIX+"."+configuration.name();
                if (properties.containsKey(key)){
                    configuration.value = properties.getProperty(key);
                    configuration.hasInitialization = true;
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * 根据数据源连接 信息来初始化数据库方言的配置
     * @param connection
     */
    public static void setDialectWithConnection(Connection connection){
        Configuration dialect = Configuration.dialect;
        if (dialect.hasInitialization){
            return;
        }
        try {
            // 通过url来判断数据库的方言
            String url = connection.getMetaData().getURL();
            if (url.startsWith("jdbc:mysql")){
                dialect.value = "mysql";
                return;
            }
            if (url.startsWith("jdbc:oracle")){
                dialect.value = "oracle";
                return;
            }
            if (url.startsWith("jdbc:postgresql")){
                dialect.value = "postgresql";
                return;
            }
            if (url.startsWith("jdbc:db2")){
                dialect.value = "db2";
                return;
            }
            if (url.indexOf("mysql") != -1){
                dialect.value = "mysql";
                return;
            }
            if (url.indexOf("oracle") != -1){
                dialect.value = "oracle";
                return;
            }
            if (url.indexOf("postgresql") != -1){
                dialect.value = "postgresql";
                return;
            }
            if (url.indexOf("db2") != -1){
                dialect.value = "db2";
                return;
            }

        } catch (Throwable e) {
        }
    }
}
