package com.abreaking.easyjpa.spring.config;

import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 *
 * @author liwei_paas
 * @date 2020/11/26
 */
@Configuration
public class EasyJpaDaoConfiguration  {

    @Bean
    public EasyJpaDao easyJpaDao(DataSource dataSource){
        return new EasyJpaDaoImpl(dataSource);
    }
}
