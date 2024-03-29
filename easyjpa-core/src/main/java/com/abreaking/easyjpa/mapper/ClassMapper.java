package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.annotation.DateTable;
import com.abreaking.easyjpa.util.ReflectUtils;
import com.abreaking.easyjpa.util.StringUtils;

import javax.persistence.Table;
import javax.persistence.Transient;
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
public final class ClassMapper {

    /**
     * 实体类
     */
    private Class obj;
    /**
     * 映射后的表名
     */
    private String tableName;
    //是否是动态表名
    private boolean isDynamicTableName = false;

    /**
     * 字段属性的映射
     */
    private FieldMapper idFieldMapper;
    private List<FieldMapper> pksFieldMapper = new ArrayList<>();
    private Map<String,FieldMapper> fieldsMapper = new HashMap<>(); //可映射成实体类的字段

    /**
     * 先暂时用个hashmap把，它应该有个缓存策略
     */
    private static final Map<Class,ClassMapper> MAPPER_CACHE = new HashMap<>();

    private ClassMapper(Class obj){
        this.obj = obj;
        initTableName(obj);
        initFieldMapper(obj);
    }

    /**
     * 初始化 实体类的属性->表字段的映射
     * @param obj
     */
    private void initFieldMapper(Class obj){
        Field[] fields = obj.getDeclaredFields();
        Map<String, Method> methodMap = ReflectUtils.poGetterMethodsMap(obj);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (!methodMap.containsKey(fieldName)){
                //如果该字段不是 getter setter字段，就不用再继续了
                continue;
            }
            if (field.isAnnotationPresent(Transient.class)){
                //如果Transient 标识的字段也不用继续了
                continue;
            }

            FieldMapper fieldMapper = new FieldMapper(field,methodMap.get(fieldName));
            fieldsMapper.put(fieldName, fieldMapper);
            fieldsMapper.put(fieldMapper.getColumnName(), fieldMapper);

            // id 、 pk等特殊字段记录
            String fieldAnnotation = fieldMapper.getFieldAnnotation();
            if (fieldAnnotation.equals(FieldMapper.FIELD_ANNOTATION_ID)){
                idFieldMapper = fieldMapper;
            }else if (fieldAnnotation.equals(FieldMapper.FIELD_ANNOTATION_PK)){
                pksFieldMapper.add(fieldMapper);
            }
        }
        //映射后的字段不可修改
        pksFieldMapper = Collections.unmodifiableList(pksFieldMapper);
        fieldsMapper = Collections.unmodifiableMap(fieldsMapper);
    }

    /**
     * 类->表名的映射
     * 先暂时不考虑动态表的问题
     * @param obj
     */
    private void initTableName(Class obj){
        if(obj.isAnnotationPresent(Table.class)){
            Table table = (Table) obj.getAnnotation(Table.class);
            tableName = table.name();
        }else if (obj.isAssignableFrom(DateTable.class)){
            isDynamicTableName = true;
            tableName = "$dateTable";
        }else{
            this.tableName =  StringUtils.underscoreName(obj.getSimpleName());
        }
    }

    /**
     * 映射实体对象
     * 它是classMapper的入口方法，通过该方法可描述实体类与表 之间的映射关系
     * @param obj
     * @return
     */
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

    /**
     * 映射 后的表名
     * 可能是动态表，每次获取表名都需要做映射
     * @return
     */
    public String mapTableName() {
        if (isDynamicTableName){
            return ReflectUtils.getDateTableName(obj,new Date());
        }else{
            return tableName;
        }
    }



    /**
     * 映射后的主键
     * @return
     */
    public FieldMapper mapId() {
        return idFieldMapper;
    }

    /**
     * 所有可映射的字段，即能够被转换为 字段->列 的所有映射关系
     * @return
     */
    public Set<FieldMapper> allMappableFields() {
        return new HashSet<>(fieldsMapper.values());
    }

    /**
     * 映射后的字段名及列名的关系
     * 可使用字段名来获取，也可以使用列名来获取
     * @param fieldOrColumnName
     * @return
     */
    public FieldMapper mapField(String fieldOrColumnName){
        return fieldsMapper.get(fieldOrColumnName);
    }
}
