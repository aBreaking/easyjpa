package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.exception.NoIdOrPkSpecifiedException;
import com.abreaking.easyjpa.exception.NoSuchFieldOrColumnException;
import com.abreaking.easyjpa.mapper.ClassMapper;
import com.abreaking.easyjpa.mapper.FieldMapper;
import com.abreaking.easyjpa.mapper.MatrixMapper;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.mapper.matrix.Matrix;
import com.abreaking.easyjpa.mapper.matrix.MatrixFactory;
import com.abreaking.easyjpa.sql.SqlConst;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 对实体的映射，接收一个实体对象，配合SqlBuilder 可将其处理成可执行的预sql
 * @author liwei_paas
 * @date 2020/12/3
 */
public abstract class BaseEasyJpa<T> implements MatrixMapper {

    // 实体的class对象
    private Class obj;

    // 实体的映射信息
    protected final ClassMapper classMapper;

    private ColumnMatrix matrix = MatrixFactory.createColumnMatrix();

    Map<SqlConst,List<Condition>> conditionMap = new HashMap<>();

    public BaseEasyJpa(T t){
        this((Class<T>) t.getClass());
        mapEntity(t);
    }

    public BaseEasyJpa(Class<T> obj){
        this.obj = obj;
        this.classMapper = ClassMapper.map(obj);
    }

    /**
     * 将实体对象的属性进行映射
     * @param entity
     */
    private void mapEntity(Object entity){
        Map<String, FieldMapper> fieldsMapper = classMapper.getFieldsMapper();
        List<Condition> list = new ArrayList<>();
        for (FieldMapper fieldMapper : fieldsMapper.values()){
            try {
                Method getterMethod = fieldMapper.getGetterMethod();
                Object value = getterMethod.invoke(entity);
                if (value!=null){
                    String columnName = fieldMapper.getColumnName();
                    int columnType = fieldMapper.getColumnType();
                    Condition condition = Condition.equal(columnName, value);
                    condition.sqlType = fieldMapper.getColumnType();
                    list.add(condition);
                    matrix.put(columnName,columnType,value);
                }
            } catch (IllegalAccessException|InvocationTargetException e) {
            }
        }
        if (!list.isEmpty()){
            conditionMap.put(SqlConst.AND,list);
        }
    }

    @Override
    public Matrix matrix() {
        return this.matrix;
    }

    protected void addCondition(SqlConst key, Condition condition){
        if (condition.isEmpty()){
            return;
        }
        if (condition.getFcName()!=null){
            String fcName = condition.fcName;
            FieldMapper mapper = classMapper.getMapper(fcName);
            if (mapper == null){
                throw new NoSuchFieldOrColumnException(getObj(),fcName);
            }
            condition.fcName = mapper.getColumnName();
            condition.sqlType = mapper.getColumnType();
        }
        List<Condition> list ;
        if (conditionMap.containsKey(key)){
            list = this.conditionMap.get(key);
        }else{
            list = new ArrayList<>();
            conditionMap.put(key,list);
        }
        list.add(condition);
    }


    public List<Condition> getConditions(SqlConst sqlConst){
        return conditionMap.get(sqlConst);
    }

    public Matrix idMatrix(){
        FieldMapper idFieldMapper = classMapper.getIdFieldMapper();
        if (idFieldMapper!=null){
            Method getterMethod = idFieldMapper.getGetterMethod();
            try {
                Object value = getterMethod.invoke(this);
                if (value!=null){
                    ColumnMatrix columnMatrix = MatrixFactory.createColumnMatrix(1);
                    columnMatrix.put(idFieldMapper.getColumnName(),idFieldMapper.getColumnType(),value);
                    return columnMatrix;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
        return null;
    }


    public String getTableName(){
        return classMapper.getTableName();
    }

    public String getIdName(){
        FieldMapper idFieldMapper = classMapper.getIdFieldMapper();
        if (idFieldMapper == null){
            throw new NoIdOrPkSpecifiedException(obj.getName()+"has no primary key! STRONGLY RECOMMEND: every table should has primary key,You can use the @Id or @Pk annotation to identify the primary key on your entity class");
        }
        return idFieldMapper.getColumnName();
    }

    public Class getObj(){
        return this.obj;
    }

}
