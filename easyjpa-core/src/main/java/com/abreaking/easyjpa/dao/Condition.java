package com.abreaking.easyjpa.dao;


import java.util.Objects;

/**
 * 将对象本身视作条件查询体。该接口可以将对象make成条件体（Matrix）以及拼接到Sql描述语句（sqlBuilder）
 * @author liwei_paas
 * @date 2020/11/26
 */
public class Condition {

    String fcName;
    String prepare;
    int sqlType;
    Object[] values;

    private Condition(String fcName, String prepare, Object[] values) {
        this.fcName = fcName;
        this.prepare = prepare;
        this.values = values;
    }

    /**
     * FIXME 这里有个问题：prepareSql可以指定什么内容呢？怎么样的格式？
     * @param prepareSql
     * @param value
     * @return
     */
    protected static Condition to(String prepareSql, Object... value) {
        return new Condition(null, prepareSql, value);
    }

    public static Condition to(String fcName, String operator, Object value) {
        if (operator.indexOf("?")==-1){
            operator += " ?";
        }
        return new Condition(fcName, operator, new Object[]{value});
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
            return Condition.to(null,null);
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

    public boolean isEmpty(){
        return this.getFcName() == null && this.getPrepare() == null;
    }

    public String getFcName() {
        return fcName;
    }

    public String getPrepare() {
        return prepare;
    }

    public Object[] getValues() {
        return values;
    }

    public int getSqlType() {
        return sqlType;
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

        return Objects.hash(fcName, prepare);
    }
}
