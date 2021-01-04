package com.abreaking.easyjpa.config;

/**
 * easyJpa的全局配置
 * @author liwei_paas
 * @date 2020/12/31
 */
public enum Configuration {

    dialect("mysql"), // 方言默认为mysql，目前暂时支持Mysql与oracle
    ;

    String value; // 默认值
    boolean hasInitialization; // 默认值是否被初始化
    Configuration(String value) {
        this(value,false);
    }
    Configuration(String value,boolean hasInitialization) {
        this.value = value;
        this.hasInitialization = hasInitialization;
    }

    public String getConfig(){
        return value;
    }

}
