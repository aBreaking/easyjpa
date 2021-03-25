package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.util.Objects;


/**
 * 缓存的key
 * @author liwei
 * @date 2021/3/1
 */
public class SelectKey {

    PreparedWrapper preparedWrapper;

    RowMapper rowMapper;

    public SelectKey(PreparedWrapper preparedWrapper, RowMapper rowMapper) {
        this.preparedWrapper = preparedWrapper;
        this.rowMapper = rowMapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectKey selectKey = (SelectKey) o;
        return Objects.equals(preparedWrapper, selectKey.preparedWrapper) &&
                Objects.equals(rowMapper, selectKey.rowMapper);
    }

    @Override
    public int hashCode() {

        return Objects.hash(preparedWrapper, rowMapper);
    }
}
