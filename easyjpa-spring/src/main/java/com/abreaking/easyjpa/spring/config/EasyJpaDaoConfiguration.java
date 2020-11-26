package com.abreaking.easyjpa.spring.config;

import com.abreaking.easyjpa.dao.EasyJpa;
import com.abreaking.easyjpa.dao.EasyJpaDao;
import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.spring.SpringJdbcTemplateExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 *
 * @author liwei_paas
 * @date 2020/11/26
 */
@Configuration
public class EasyJpaDaoConfiguration {

    @Resource
    SpringJdbcTemplateExecutor springJdbcTemplateExecutor;

    @Bean
    public <T> EasyJpaDao<T> easyJpaDao(){
        return new EasyJpaDao<T>(){

            @Override
            public EasyJpa<T> build(Class obj) {
                return null;
            }

            @Override
            public EasyJpa<T> build(T t) {
                return null;
            }
        };
    }
}
