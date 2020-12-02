package com.abreaking.easyjpa.spring.config;

import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import com.abreaking.easyjpa.spring.SpringJdbcTemplateExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author liwei_paas
 * @date 2020/11/26
 */
@Configuration
public class EasyJpaDaoConfiguration {

    @Resource
    DataSource dataSource;

    @Bean
    public <T> EasyJpaDao<T> easyJpaDao(){
        SpringJdbcTemplateExecutor executor = new SpringJdbcTemplateExecutor(dataSource,true);
        return new EasyJpaDaoImpl<>(executor);
    }

}
