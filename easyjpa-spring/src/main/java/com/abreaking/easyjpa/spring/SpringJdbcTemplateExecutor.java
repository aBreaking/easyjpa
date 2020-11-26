package com.abreaking.easyjpa.spring;

import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 使用spring JdbcTemplate的执行器
 * @author liwei_paas
 * @date 2020/11/12
 */
@Component
public class SpringJdbcTemplateExecutor implements SqlExecutor {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public <T> List<T> queryForList(String preparedSql, Object[] values, int[] types, RowMapper<T> rowMapper) {
        return jdbcTemplate.query(preparedSql, values, types, (rs, rowNum) -> rowMapper.mapRow(rs, rowNum));
    }

    @Override
    public int update(String preparedSql, Object[] values, int[] types){
        return jdbcTemplate.update(preparedSql, values, types);
    }

}
