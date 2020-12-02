package com.abreaking.easyjpa.spring;

import com.abreaking.easyjpa.executor.SqlExecutor;
import com.abreaking.easyjpa.mapper.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * 使用spring JdbcTemplate的执行器
 * @author liwei_paas
 * @date 2020/11/12
 */
public class SpringJdbcTemplateExecutor extends JdbcTemplate implements SqlExecutor {

    public SpringJdbcTemplateExecutor(DataSource dataSource) {
        super(dataSource);
    }

    public SpringJdbcTemplateExecutor(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }

    @Override
    public <T> List<T> doQuery(String preparedSql, Object[] values, int[] types, RowMapper<T> rowMapper) {
        return query(preparedSql, values, types, (rs, rowNum) -> rowMapper.mapRow(rs, rowNum));
    }

    @Override
    public int doUpdate(String preparedSql, Object[] values, int[] types){
        return update(preparedSql, values, types);
    }


}
