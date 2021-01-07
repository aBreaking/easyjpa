package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;

/**
 * test for easyDao
 * @author liwei_paas
 * @date 2020/12/30
 */
public class EasyJpaDaoTest  {

    EasyJpaDao dao = new EasyJpaDaoImpl(MyDaoTest.sqlExecutor);



}
