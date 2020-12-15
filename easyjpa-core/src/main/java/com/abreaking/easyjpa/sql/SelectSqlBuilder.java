package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.AbstractEasyJpa;
import com.abreaking.easyjpa.dao.condition.Entry;

import java.util.Collection;

/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class SelectSqlBuilder extends MatrixSqlBuilder {
    @Override
    protected void doVisit(AbstractEasyJpa easyJpa) {
        String tableName = easyJpa.getTableName();
        Collection<Entry> entryList = easyJpa.entry();
        add("SELECT * FROM ",tableName);
        if (!entryList.isEmpty()){
            add(" WHERE 1=1 ");
            for (Entry entry : entryList){
                add(" and ");
                add(entry.getColumnName());
                add(entry.getOperator());
                add("?",entry.getValue());
            }
        }
    }
}
