package com.abreaking.easyjpa.dao.cache;

import com.abreaking.easyjpa.dao.prepare.PreparedWrapper;
import com.abreaking.easyjpa.mapper.RowMapper;

import java.util.Objects;

/**
 * 缓存的key
 * @author liwei
 * @date 2021/3/1
 */
public class CacheKey {

    PreparedWrapper preparedWrapper;

    RowMapper rowMapper;

    public CacheKey(PreparedWrapper preparedWrapper, RowMapper rowMapper) {
        this.preparedWrapper = preparedWrapper;
        this.rowMapper = rowMapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return Objects.equals(preparedWrapper, cacheKey.preparedWrapper) &&
                Objects.equals(rowMapper, cacheKey.rowMapper);
    }

    @Override
    public int hashCode() {

        return Objects.hash(preparedWrapper, rowMapper);
    }
}
