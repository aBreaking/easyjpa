package com.abreaking.easyjpa.mapper.matrix;



/**
 * 用空间坐标轴的方式来描述ColumnMatrix，想象一下，空间上任意一点我们都可以用x y z三个坐标轴进行描述
 * ColumnAxisMatrix坐标系的构造交给Axis内部类去实现
 * @author liwei_paas
 * @date 2019/8/28
 */
public class AxisColumnMatrix implements ColumnMatrix {
    /**
     * 看作空间的坐标系把，x->name,y->type,z->value
     */
    private Axis<String,Integer,Object>[] ntv ;

    /**
     * ntv 的大小
     */
    transient int size;

    /**
     * 当前的ntv实际大小
     */
    int currentSize = 0;

    /**
     * 增长空间
     */
    int groupUp;

    /**
     * 默认ntv初始大小
     */
    private static final int DEFAULT_SIZE = 8;

    /**
     * 默认增长速度
     */
    private static final int DEFAULT_GROUP_UP_RATE = DEFAULT_SIZE>>1;

    public AxisColumnMatrix(){
        this(DEFAULT_SIZE);
    }

    public AxisColumnMatrix(int size){
        this(size,DEFAULT_GROUP_UP_RATE);
    }

    public AxisColumnMatrix(int size, int groupUp){
        this.size = size;
        this.groupUp = groupUp;
        ntv =  new Axis[size];
    }

    public void put(String name,int type,Object value){
        if (currentSize==size){
            groupUp();
        }
        ntv[currentSize++] = new Axis(name,type,value);
    }


    @Override
    public void remove(String column) {
        int i = indexOf(column);
        if (i!=-1){
            for (int j = i; j < ntv.length-1; j++) {
                ntv[j] = ntv[j+1];
            }
            currentSize--;
        }
    }

    @Override
    public int indexOf(String column) {
        for (int i = 0; i < currentSize; i++) {
            if (ntv[i].xAxis.equals(column)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getType(int index) {
        return axis(index).yAxis;
    }

    @Override
    public Object getValue(int index) {
        return axis(index).zAxis;
    }

    @Override
    public String getColumn(int index) {
        return axis(index).xAxis;
    }

    private Axis<String,Integer,Object> axis(int index){
        if (index>currentSize){
            throw new ArrayIndexOutOfBoundsException("Array index out of range: " + index+" ,current size is: "+currentSize);
        }
        return ntv[index];
    }

    public String[] columns(){
        String[] ret = new String[currentSize];
        for (int i = 0; i < currentSize; i++) {
            ret[i] = ntv[i].xAxis;
        }
        return ret;
    }
    public int[] types(){
        int[] ret = new int[currentSize];
        for (int i = 0; i < currentSize; i++) {
            ret[i] = ntv[i].yAxis;
        }
        return ret;
    }
    public Object[] values(){
        Object[] ret = new Object[currentSize];
        for (int i = 0; i < currentSize; i++) {
            ret[i] = ntv[i].zAxis;
        }
        return ret;
    }

    @Override
    public boolean isEmpty() {
        return currentSize == 0;
    }

    protected void groupUp(){
        this.size += groupUp;
        Axis<String,Integer,Object>[] newNtv = new Axis[size];
        for (int i = 0; i < currentSize; i++) {
            newNtv[i] = ntv[i];
        }
        this.ntv = newNtv;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AxisColumnMatrix:[");
        for (int i = 0; i < currentSize; i++) {
            Axis axis = ntv[i];
            builder.append(axis.xAxis)
                    .append(":").append(axis.yAxis).append(":").append(axis.zAxis);
            builder.append(";");
        }
        String s = builder.toString();
        if (s.endsWith(";")){
            s = s.substring(0,s.length()-1);
        }
        s += "]";
        return s;
    }

    /**
     * 一个三位数组的数据结构，想象一下x轴、y轴、z轴的空间表示
     * @author liwei_paas
     * @date 2019/7/4
     */
    private class Axis<X,Y,Z>{
        final X xAxis ;
        final Y yAxis;
        final Z zAxis;

        public Axis(X x,Y y,Z z){
            xAxis = x;
            yAxis = y;
            zAxis = z;
        }

    }
}
