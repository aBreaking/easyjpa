package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * test for easyDao
 * @author liwei_paas
 * @date 2020/12/30
 */
public class EasyJpaDaoTest  {

    EasyJpaDao dao = new EasyJpaDaoImpl(MyDaoTest.sqlExecutor);


}
