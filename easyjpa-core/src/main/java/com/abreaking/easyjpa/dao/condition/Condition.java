package com.abreaking.easyjpa.dao.condition;

import com.abreaking.easyjpa.mapper.matrix.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 条件语句的描述 。 也是prepareSql 片段，它用在EasyJpa中，用来将实体对象描述成sql片段
 * 比如用来描述如下的sql： user_name like ?
 * @author liwei_paas
 * @date 2020/11/26
 */
public class Condition {

    String fcName; //列名,easyjpa也支持字段名

    Integer sqlType ; //列类型

    String prepare; //操作符预处理语句，比如 =? 、 like ? 、 in (?,?)

    Object[] values; // 对应的值

    public Condition() {
    }

    public Condition(String fcName, String prepare, Object...values) {
        this.fcName = fcName;
        this.prepare = prepare;
        this.values = values;
    }

    /**
     * 可直接写入prepareSql，以及value
     * 这里有个问题：prepareSql可以指定什么内容呢？怎么样的格式？ 是否应该开放该功能？
     * @param prepareSql
     * @param values
     * @return
     */
    public static Condition prepare(String prepareSql, Object...values) {
        return new Condition(null, prepareSql, values);
    }

    /**
     * 基本条件，会自动将条件转为预预处理的sql
     * @param fcName 字段名或列名
     * @param operator 操作符，比如 > < = like 等等
     * @param value 值
     * @return
     */
    public static Condition to(String fcName, String operator, Object value) {
        if (operator.toUpperCase().equals("LIKE")){
            return like(fcName,String.valueOf(value));
        }
        if (operator.indexOf("?")==-1){
            operator += " ?";
        }
        return new Condition(fcName, operator, value);
    }

    public static Condition equal(String fcName, Object value) {
        return new Condition(fcName, "= ?", new Object[]{value});
    }

    public static Condition like(String fcName, String value) {
        if (value.indexOf("%")==-1){
            value = "%"+value+"%";
        }
        return new Condition(fcName, "LIKE ?", new Object[]{value});
    }

    public static Condition in(String fcName, Object...values) {
        if (values.length==0){
            return Condition.prepare(null,null);
        }
        StringBuilder builder = new StringBuilder("IN (");
        for (int i = 0; i < values.length; i++) {
            builder.append("?,");
        }
        builder.replace(builder.lastIndexOf(","),builder.length(),")");
        return new Condition(fcName, builder.toString(), values);
    }

    public static Condition between(String fcName, Object s, Object b) {
        return new Condition(fcName, "BETWEEN ? AND ?", new Object[]{s, b});
    }

    public static Condition isNull(String fcName){
        return new Condition(fcName, " IS NULL");
    }

    public static Condition isNotNull(String fcName){
        return new Condition(fcName, " IS NOT NULL");
    }

    public String getFcName() {
        return fcName;
    }

    public void setFcName(String fcName) {
        this.fcName = fcName;
    }

    public Integer getSqlType() {
        return sqlType;
    }

    public void setSqlType(Integer sqlType) {
        this.sqlType = sqlType;
    }

    public String getPrepare() {
        return prepare;
    }

    public void setPrepare(String prepare) {
        this.prepare = prepare;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    /**
     * 将matrix 转为condition
     * @param matrix
     * @return
     */
    public static List<Condition> matrixToCondition(Matrix matrix){
        List<Condition> list = new ArrayList<>();
        if (matrix!=null){
            String[] columns = matrix.columns();
            int[] types = matrix.types();
            Object[] values = matrix.values();
            for (int i = 0; i < columns.length; i++) {
                Condition condition = Condition.to(columns[i], "=", values[i]);
                condition.sqlType = types[i];
                list.add(condition);
            }
        }
        return list;
    }

    /**
     * 将condition格式化，因为此时condition里的fcName不知道是列名，还是字段名
     * @param condition
     */
    public static void formatCondition(Condition condition,String columnName,int type){
        condition.fcName = columnName;
        condition.sqlType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return Objects.equals(fcName, condition.fcName) &&
                Objects.equals(prepare, condition.prepare);
    }

    @Override
    public int hashCode() {
        return (31*Objects.hash(fcName, prepare,sqlType))+Arrays.hashCode(values);
    }
}
