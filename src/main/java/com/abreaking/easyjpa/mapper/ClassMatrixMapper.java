package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.mapper.annotation.Column;
import com.abreaking.easyjpa.mapper.annotation.Id;
import com.abreaking.easyjpa.mapper.annotation.Pk;
import com.abreaking.easyjpa.mapper.annotation.Table;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.AxisColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.IndexColumnMatrix;
import com.abreaking.easyjpa.util.StringUtils;
import com.abreaking.easyjpa.util.ReflectUtil;
import com.abreaking.easyjpa.util.SqlUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 通用的JPA 实体-表  映射类
 * 通过它你就不需要再麻烦的一一映射 属性->列名 了。
 * 如果你想继承该类，那么你得需要遵守以下的规则：
 *      1、规范化的命名你的字段还有你的类名，驼峰式，比如：你数据库某个表的字段叫做 aaa_bbb_ccc，那么你的字段名就应该是aaaBbbCcc，类名跟表名也是一样的，类名允许跟个后缀 vo 或者 po
 *      2、类名跟表名的映射你也可以使用 DateTable注解，或者你去重写defaultTableName()这个方法。
 *      3、每个表都强烈建议有个自然主键，会被自动分配，默认是自增。这在你做增删改查很有用。默认主键名叫做 id 。。
 * 目前规则很简单，也就是尽量去遵守java 开发的相关规范就OK了。
 *
 * @author liwei_paas
 */
public final class ClassMatrixMapper implements MatrixMapper{

    private Field id;
    private List<Field> pks = new ArrayList<>();
    private List<Field> mappingFields = new ArrayList<>(); //可映射成实体类的字段

    private String tableName;
    private ColumnMatrix idMatrix = new AxisColumnMatrix(1);
    private ColumnMatrix pksMatrix = new AxisColumnMatrix();
    private ColumnMatrix columnsMatrix = new AxisColumnMatrix();
    // 字段与列名的映射关系,它不区分字段名与列名的
    private Map<String,String> fctMap = new HashMap<>();

    /**
     * 先暂时用个hashmap把，它应该有个缓存策略
     */
    private static final Map<Class,ClassMatrixMapper> MAPPER_CACHE = new HashMap<>();

    public static ClassMatrixMapper map(Class obj){
        if (!MAPPER_CACHE.containsKey(obj)){
            synchronized (MAPPER_CACHE){
                if (!MAPPER_CACHE.containsKey(obj)){
                    ClassMatrixMapper classMatrixMapper = new ClassMatrixMapper(obj);
                    MAPPER_CACHE.put(obj, classMatrixMapper);
                }
            }
        }
        return MAPPER_CACHE.get(obj);
    }

    // 应该对 obj 缓存起来
    private ClassMatrixMapper(Class obj) {
        this.initTableName(obj);
        this.initMatrixAndFields(obj);
    }

    public String tableName(){
        return this.tableName;
    }
    public ColumnMatrix mapId(){
        return this.idMatrix;
    }
    public ColumnMatrix mapPks(){
        return this.pksMatrix;
    }
    public ColumnMatrix matrix(){
        return this.columnsMatrix;
    }
    public String getColumnAndType(String filedName){
        return fctMap.get(filedName);
    }

    public Field getId() {
        return id;
    }

    public List<Field> getPks() {
        return pks;
    }

    public List<Field> getMappingFields() {
        return mappingFields;
    }

    /**
     * 表名
     * @return
     */
    private void initTableName(Class obj){
        if(obj.isAnnotationPresent(Table.class)){
            Table table = (Table) obj.getAnnotation(Table.class);
            this.tableName = table.value();
        }else{
            this.tableName =  StringUtils.underscoreName(obj.getSimpleName());
        }
    }

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
            int type = Types.NULL;

            //字段上的注解处理
            if (field.isAnnotationPresent(Id.class)) {
                Id id = field.getAnnotation(Id.class);
                columnName = id.value();
                type = id.type();
                this.id = field;
                this.idMatrix.put(columnName,type,null);
            }else if (field.isAnnotationPresent(Pk.class)) {
                Pk pk = field.getAnnotation(Pk.class);
                columnName = pk.value();
                type = pk.type();
                this.pks.add(field);
                this.pksMatrix.put(columnName,type,null);
            }else if (field.isAnnotationPresent(Column.class)){
                Column column = field.getAnnotation(Column.class);
                columnName = column.value();
                type = column.type();
            }
            //列名及为空的话就采用，默认操作
            if (StringUtils.isEmpty(columnName)){
                columnName = StringUtils.underscoreName(field.getName());
            }
            if (type == Types.NULL){
                type = SqlUtil.getSqlType(field.getType());
            }
            this.mappingFields.add(field);
            this.columnsMatrix.put(columnName,type,null);
            this.fctMap.put(fieldName,columnName+":"+type);
            this.fctMap.put(columnName,columnName+":"+type);
        }
    }
}
