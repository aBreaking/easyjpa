package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.annotation.Column;
import com.abreaking.easyjpa.mapper.annotation.Id;
import com.abreaking.easyjpa.mapper.annotation.Pk;
import com.abreaking.easyjpa.mapper.annotation.Table;
import com.abreaking.easyjpa.util.ReflectUtil;
import com.abreaking.easyjpa.util.SqlUtil;
import com.abreaking.easyjpa.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 用来描述实体-表 的映射关系
 * 通过它你可以一次性搞定  属性名->列名，类名->表名的 映射  了
 * 如果你想继承该类，那么你得需要遵守以下的规则：
 *      1、规范化的命名你的字段还有你的类名，驼峰式，比如：你数据库某个表的字段叫做 aaa_bbb_ccc，那么你的字段名就应该是aaaBbbCcc，类名跟表名也是一样的，类名允许跟个后缀 vo 或者 po
 *      2、类名跟表名的映射你也可以使用 DateTable注解，或者你去重写defaultTableName()这个方法。
 *      3、每个表都强烈建议有个自然主键，会被自动分配，默认是自增。这在你做增删改查很有用。默认主键名叫做 id 。。
 * 目前规则很简单，也就是尽量去遵守java 开发的相关规范就OK了。
 *
 * @author liwei_paas
 */
public class ClassMapper {

    /**
     * 实体类
     */
    private Class obj;
    /**
     * 映射后的表名
     */
    private String tableName;

    /**
     * 字段属性的映射
     */
    private Mapper idMapper;
    private List<Mapper> pksMapper = new ArrayList<>();
    private Map<String,Mapper> fieldsMapper = new HashMap<>(); //可映射成实体类的字段

    /**
     * 先暂时用个hashmap把，它应该有个缓存策略
     */
    private static final Map<Class,ClassMapper> MAPPER_CACHE = new HashMap<>();

    private ClassMapper(Class obj){
        this.obj = obj;
        initTableName(obj);
        initMatrixAndFields(obj);
        this.pksMapper = Collections.unmodifiableList(this.pksMapper);
        this.fieldsMapper = Collections.unmodifiableMap(fieldsMapper);
    }

    public static ClassMapper map(Class obj){
        if (!MAPPER_CACHE.containsKey(obj)){
            synchronized (MAPPER_CACHE){
                if (!MAPPER_CACHE.containsKey(obj)){
                    ClassMapper classMapper = new ClassMapper(obj);
                    MAPPER_CACHE.put(obj, classMapper);
                }
            }
        }
        return MAPPER_CACHE.get(obj);
    }

    public String getTableName() {
        return tableName;
    }

    public Mapper getIdMapper() {
        return idMapper;
    }

    public List<Mapper> getPksMapper() {
        return pksMapper;
    }

    public Map<String,Mapper> getFieldsMapper() {
        return fieldsMapper;
    }
    public Mapper getMapper(String fieldOrColumnName){
        return fieldsMapper.get(fieldOrColumnName);
    }

    /**
     * 初始化 实体类的属性->表字段的映射
     * @param obj
     */
    private void initMatrixAndFields(Class obj){
        Field[] fields = obj.getDeclaredFields();
        Map<String, Method> methodMap = ReflectUtil.poGetterMethodsMap(obj);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (!methodMap.containsKey(fieldName)){
                //如果该字段不是 getter setter字段，就不用再继续了
                continue;
            }
            field.setAccessible(true);
            String columnName = null;
            int type = 0;
            Mapper mapper = new Mapper();
            //字段上的注解处理
            if (field.isAnnotationPresent(Id.class)) {
                Id id = field.getAnnotation(Id.class);
                columnName = id.value();
                type = id.type();
                idMapper = mapper;
            }else if (field.isAnnotationPresent(Pk.class)) {
                Pk pk = field.getAnnotation(Pk.class);
                columnName = pk.value();
                type = pk.type();
                pksMapper.add(mapper);
            }else if (field.isAnnotationPresent(Column.class)){
                Column column = field.getAnnotation(Column.class);
                columnName = column.value();
                type = column.type();
            }
            if (StringUtils.isEmpty(columnName)){
                columnName = StringUtils.underscoreName(fieldName);
            }
            if (type == 0){
                type = SqlUtil.getSqlType(field.getType());
            }
            fieldsMapper.put(fieldName,mapper);
            fieldsMapper.put(columnName,mapper);
            mapper.setField(field);
            mapper.setColumnType(type);
            mapper.setColumnName(columnName);
            mapper.setGetterMethod(methodMap.get(fieldName));
        }
    }

    /**
     * 类->表名的映射
     * 先暂时不考虑动态表的问题
     * @param obj
     */
    private void initTableName(Class obj){
        if(obj.isAnnotationPresent(Table.class)){
            Table table = (Table) obj.getAnnotation(Table.class);
            this.tableName = table.value();
        }else{
            this.tableName =  StringUtils.underscoreName(obj.getSimpleName());
        }
    }
}
