package com.abreaking.easyjpa.dao;


/**
 * 默认的Condition实现
 * @author liwei_paas
 * @date 2020/11/13
 */
public class EasyJpa<T> extends AbstractEasyJpa{

    public EasyJpa(T o) {
        super(o);
    }

    public EasyJpa(Class obj) {
        super(obj);
    }

    public void addValues(String filedName, Object value){
        add(filedName,"=",value);
    }

    public void addLike(String filedName,String value){
        add(filedName,"like",value);
    }

    public void setLike(String filedName){
        set(filedName,"like");
    }

    public void setGt(String filedName){
        set(filedName,">");
    }

    public void addGt(String filedName,Object value){
        add(filedName,">",value);
    }

}
