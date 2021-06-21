package com.abreaking.easyjpa.spring.config;

import com.abreaking.easyjpa.config.EasyJpaConfiguration;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import javax.sql.DataSource;
import java.util.Map;

/**
 *
 * 通过加载配置文件来初始化easyJpaDao
 * 你可以将easyJpa 的配置文件放在spring中，比如：@PropertySource("applicaiton.properties")
 *
 * @author liwei_paas
 * @date 2020/11/26
 */
@Configuration
public class EasyJpaDaoConfiguration  {

    @Bean
    public EasyJpaDao easyJpaDao(DataSource dataSource, ConfigurableEnvironment environment){
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource propertySource : propertySources){
            Object source = propertySource.getSource();
            if (source instanceof Map){
                EasyJpaConfiguration.initConfiguration((Map) source);
            }
        }
        return new EasyJpaDaoImpl(dataSource);
    }
}
