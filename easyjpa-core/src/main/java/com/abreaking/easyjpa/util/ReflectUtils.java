package com.abreaking.easyjpa.util;


import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.mapper.annotation.DateTable;
import org.apache.commons.lang.time.FastDateFormat;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 反射工具
 * @author liwei_paas
 * @date 2019/11/1
 */
public final class ReflectUtils {

    /**
     * 针对日期表，根据指定日期获取映射后的表名
     * @param date
     * @return
     */
    public static String getDateTableName(Class obj,Date date){
        if (obj.isAssignableFrom(DateTable.class)){
            DateTable table = (DateTable) obj.getAnnotation(DateTable.class);
            String prefix = table.tablePrefix();
            String format = table.dateFormat();
            FastDateFormat dateFormat = FastDateFormat.getInstance(format);
            return prefix+dateFormat.format(date)+table.tableSuffix();
        }
        throw new EasyJpaException(obj+"必须指定@DateTable注解");
    }

    /**
     * 带有getter及setter的属性的getter方法。class中所有的
     * @param clazz
     * @return
     */
    public static List<Method> poGetterMethods(Class clazz){
        List<Method> list= new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        Set<String> methodNameSet = new HashSet(methods.length);
        for (Method method : methods){
            String methodName = method.getName();
            if (methodName.startsWith("set")){
                String getMethodName = "g"+methodName.substring(1);
                if (methodNameSet.contains(getMethodName)){
                    try{
                        list.add(clazz.getDeclaredMethod(getMethodName));
                        continue;
                    }catch (Exception e){}
                }
            }
            if (methodName.startsWith("get")){
                String setMethodName = "s"+methodName.substring(1);
                if (methodNameSet.contains(setMethodName)){
                    list.add(method);
                }
            }
            methodNameSet.add(methodName);
        }
        return list;
    }

    /**
     * po类所有的带有getter setter字段的getter方法
     * @param clazz
     * @return map: key:字段名，equal:getter 方法
     */
    public static Map<String,Method> poGetterMethodsMap(Class clazz){
        Map<String,Method> map = new HashMap<>();
        Method[] methods = clazz.getDeclaredMethods();
        Set<String> methodNameSet = new HashSet(methods.length);
        for (Method method : methods){
            String methodName = method.getName();
            if (methodName.startsWith("set")){
                //get方法名
                String getMethodName = "g"+methodName.substring(1);
                //get方法对应的字段名
                String filedName = filedName(methodName);
                if (methodNameSet.contains(getMethodName)){
                    try{
                        map.put(filedName,clazz.getDeclaredMethod(getMethodName));
                        continue;
                    }catch (Exception e){}
                }
            }
            if (methodName.startsWith("get")){
                String setMethodName = "s"+methodName.substring(1);
                if (methodNameSet.contains(setMethodName)){
                    map.put(filedName(methodName),method);
                }
            }
            methodNameSet.add(methodName);
        }
        return map;
    }

    private static String filedName(String getterOrSetterMethodName){
        String fieldName = getterOrSetterMethodName.substring(3);
        String firstWord = fieldName.substring(0, 1);
        String suffix = fieldName.substring(1);
        return firstWord.toLowerCase()+suffix;
    }

}
