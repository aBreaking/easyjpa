package com.abreaking.easyjpa.executor;


import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.util.List;
import java.util.function.Supplier;

/**
 * easy-jpa sql执行器
 * @author liwei
 * @date 2021/6/25
 */
public interface EasyJpaExecutor {

    <T> List<T> query(Supplier<PreparedWrapper> sqlSupplier, RowMapper rowMapper);

    void execute(Supplier<PreparedWrapper> sqlSupplier);

}
