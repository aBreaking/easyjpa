package com.abreaking.easyjpa.config;

/**
 * easyJpa的全局配置
 * @author liwei_paas
 * @date 2020/12/31
 */
public enum Configuration {

    cache("lru"),
    cache_lru_max_size("128"),
    ;

    String value; // 默认值
    boolean hasInitialization; // 默认值是否被初始化
    Configuration(String value) {
        this(value,false);
    }
    Configuration(String value,boolean hasInitialization) {
        this.value = value.toLowerCase();
        this.hasInitialization = hasInitialization;
    }

    public String getConfig(){
        return value;
    }

}
