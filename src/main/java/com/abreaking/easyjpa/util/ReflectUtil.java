package com.abreaking.easyjpa.util;


import java.lang.reflect.Method;
import java.util.*;

/**
 * 反射工具
 * @author liwei_paas
 * @date 2019/11/1
 */
public final class ReflectUtil {


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
     * @return map: key:字段名，value:getter 方法
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
