package com.abreaking.easyjpa.executor;

import com.abreaking.easyjpa.mapper.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * 使用spring JdbcTemplate的执行器
 * @author liwei_paas
 * @date 2020/11/12
 */
public class SpringJdbcTemplateExecutor implements SqlExecutor{

    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    public SpringJdbcTemplateExecutor(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public <T> List<T> queryForList(String preparedSql, Object[] values, int[] types, RowMapper<T> rowMapper) {
        return jdbcTemplate.query(preparedSql, values, types, (rs, rowNum) -> rowMapper.mapRow(rs, rowNum));
    }

    @Override
    public int update(String preparedSql, Object[] values, int[] types){
        return jdbcTemplate.update(preparedSql, values, types);
    }

}
