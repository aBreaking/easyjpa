package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.AbstractEasyJpa;
import com.abreaking.easyjpa.dao.condition.Page;

/**
 *
 * @author liwei_paas
 * @date 2020/12/15
 */
public class PageSqlBuilder extends SelectSqlBuilder {

    private Page page;
    //分页默认 mysql的语法
    private String dialect = "mysql";

    public PageSqlBuilder(Page page) {
        this.page = page;
    }


    @Override
    protected void doVisit(AbstractEasyJpa easyJpa) {
        super.doVisit(easyJpa);
        this.doVisitMysqlPage(page);
    }

    protected void doVisitMysqlPage(Page page){
        String orderBy = page.getOrderBy();
        if (orderBy!=null){
            add(" ORDER BY ",page.getOrderBy());
        }
        if (dialect.equals("mysql")){
            add(" limit ?,? ",page.getPageNum(),page.getPageSize());
        }
    }
}
