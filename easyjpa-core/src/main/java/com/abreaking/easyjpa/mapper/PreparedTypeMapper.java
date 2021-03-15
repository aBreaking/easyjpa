package com.abreaking.easyjpa.mapper;

import com.abreaking.easyjpa.exception.EntityObjectNeedsException;
import com.abreaking.easyjpa.util.SqlUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 *  指定返回类型，映射每行的数据，而后进行返回指定类型
 *  根据你指定的returnTypes 有如下返回类型：
 *  1. 没有指定returnTypes ， 或者类型指定有误。默认返回 "Map<String,Object>"类型的数据，string 是列名，object是对应的值（会根据返回值类型转换为Java的类型）
 *  2. 指定了一个returnTypes，并且该类型为基本类型，并且只有一列返回结果！ 那么会返回该基本类型的数据；
 *  3. 指定了一个returnTypes，并且该类型为实体类的类型。 那么会返回该实体类对象，row的数据会自动封装到返回对象中去；
 *  4. 指定了若干returnTypes，并且都是基本类型，那么会返回"Map<String,Object>"类型的数据，其中object值的类型为你指定的数据类型
 *  5. 指定了若干returnTypes，并且都是实体类的类型，那么返回一个数组，数组里的每个元素为你指定类型的对象，row的数据会自动封装到返回对象中去。
 *  6. 其他情况，全部默认返回 第1个所说的情况。
 *
 *  简单来说就总共只有如下四种返回类型：
 *  1. 返回一个指定的基本类型的对象，指定了一个基本类型的returnType，并且sql执行结果只有一列的数据；
 *  2. 返回一个实体类的对象：指定了一个实体对象的returnType;
 *  3. 返回一个实体类对象的数组：指定了若干实体对象的returnType;
 *  4. 其他情况 均返回"Map<String,Object>"
 *
 * @author liwei_paas
 * @date 2021/1/6
 */
public class PreparedTypeMapper implements RowMapper {

    private Class[] returnTypes;

    public PreparedTypeMapper(Class[] returnTypes) {
        this.returnTypes = returnTypes;
    }

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (returnTypes!=null ){
            if (returnTypes.length==1){
                Class returnType = returnTypes[0];
                if (SqlUtil.getSqlType(returnType)==0){
                    try {
                        return new ClassRowMapper(returnType).mapRow(rs,rowNum);
                    }catch (EntityObjectNeedsException e){
                        // 既不是正常的实体类，也不是基本类型，那么就最后默认处理了
                    }
                }else{
                    //说明不是实体类，那么就用javaRowMapper来映射，并且最后只返回这个类型的值
                    Map map = new JavaMapRowMapper(returnTypes).mapRow(rs, rowNum);
                    return map.size() == 1?map.values().toArray()[0]:map;
                }
            }else{
                // 先判断所有的类型可不可能是实体类型
                boolean isEntity = false;
                for (Class returnType : returnTypes){
                    if (!(isEntity = SqlUtil.getSqlType(returnType)==0)){
                        break;
                    }
                }
                if (isEntity){
                    try{
                        Object[] entities = new Object[returnTypes.length];
                        for (int j = 0; j < returnTypes.length; j++) {
                            entities[j] = new ClassRowMapper(returnTypes[j]).mapRow(rs, rowNum);
                        }
                        return entities;
                    }catch (EntityObjectNeedsException e){
                        // 同上
                    }
                }
            }
        }
        //默认返回java map的类型
        return new JavaMapRowMapper(returnTypes).mapRow(rs,rowNum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreparedTypeMapper that = (PreparedTypeMapper) o;
        return Arrays.equals(returnTypes, that.returnTypes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(returnTypes);
    }
}
