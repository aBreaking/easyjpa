package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.prepare.PreparedWrapper;
import com.abreaking.easyjpa.support.EasyJpa;
import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.exception.EasyJpaException;
import com.abreaking.easyjpa.mapper.matrix.ColumnMatrix;
import com.abreaking.easyjpa.util.SqlUtil;
import com.abreaking.easyjpa.util.StringUtils;

import java.sql.Types;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * prepareSql的默认实现
 * 它支持直接传入可执行的sql
 * @author liwei_paas
 * @date 2020/12/29
 */
public class PlaceHolderSqlBuilder implements SqlBuilder{

    private static final Pattern pattern = Pattern.compile("(\\$|#)\\{\\w+}");

    PlaceholderMapper placeholderMapper;

    public PlaceHolderSqlBuilder(PlaceholderMapper placeholderMapper){
        this.placeholderMapper = placeholderMapper;
    }

    @Override
    public PreparedWrapper visit(Conditions conditions) {
        StringBuilder sqlBuilder = new StringBuilder();
        return null;
    }

    protected void doVisit(StringBuilder sqlBuilder,ColumnMatrix columnMatrix) {
        Map<String, Object> argsMap = placeholderMapper.getArgsMap();
        List<PlaceholderMapper.Fragment> fragments = placeholderMapper.getSqlFragmentList();
        if (fragments.isEmpty()){
            throw new EasyJpaException("no placeholder sql statement specified");
        }

        //有一种情况，传入的是一条sql
        if (fragments.size()==1){
            String fragmentSql = fragments.get(0).getFragmentSql();
            //一条完整的sql就不再进行notStay判断了
            sqlBuildIfStay(sqlBuilder,columnMatrix,fragmentSql,argsMap,key->true);
            return;
        }

        for (PlaceholderMapper.Fragment fragment : fragments){
            String fragmentSql = fragment.getFragmentSql();
            String argKey = fragment.getArgKey();
            Object rule = fragment.getRule();

            int sqlBuilderStart = sqlBuilder.length();
            if (StringUtils.isNotEmpty(argKey)){
                if (notStay(argsMap, argKey, rule)) continue;
            }
            sqlBuildIfStay(sqlBuilder,columnMatrix,fragmentSql,argsMap,key->{
                if (notStay(argsMap,key,null)){
                    sqlBuilder.delete(sqlBuilderStart,sqlBuilder.length());
                    return false;
                }
                return true;
            });
        }
        if (sqlBuilder.length() == 0){
            throw new EasyJpaException("The placeholder parameter parsing failed. All the placeholder parameters have no specified values or the definition rules are incorrect. The placeholder SQL is："+placeholderMapper.toPlaceholderSql());
        }
    }

    /**
     * 解析占位符，并组装sql
     * @param columnMatrix
     * @param placeholderFragmentSql
     * @param argsMap
     * @param bt
     */
    private void sqlBuildIfStay(StringBuilder sqlBuilder,ColumnMatrix columnMatrix, String placeholderFragmentSql, Map<String, Object> argsMap,Function<String,Boolean> bt){
        Matcher matcher = pattern.matcher(placeholderFragmentSql);
        int i = 0;
        while (matcher.find()){
            String group = matcher.group();
            String argKey = group.substring(2,group.length()-1);
            if (!bt.apply(argKey)){
                return;
            }
            Object value = argsMap.get(argKey); // 这里value可能为空值
            sqlBuilder.append(placeholderFragmentSql, i, matcher.start());
            if (group.startsWith("${")){
                if (value==null) throw new EasyJpaException("The placeholder parameter '"+argKey+"' does not specify the corresponding value of the parameter or its value may be NULL. The placeholder sql is："+placeholderFragmentSql);;
                sqlBuilder.append(value);
            }else{
                sqlBuilder.append("?");
                columnMatrix.put(argKey,SqlUtil.getSqlTypeByValue(value),value);
            }
            i = matcher.end();
        }
        sqlBuilder.append(placeholderFragmentSql,i,placeholderFragmentSql.length());
        sqlBuilder.append(" ");
    }


    /**
     * 根据rule来决定 key是否留下
     * @param argsMap
     * @param key
     * @param rule
     * @return
     */
    private boolean notStay(Map<String, Object> argsMap,String key,Object rule){
        if (argsMap.containsKey(key)){
            Object o = argsMap.get(key);
            Class<?> type = o.getClass();
            if (type.equals(String.class)) return StringUtils.isEmpty((String)o);
            if (type.isAssignableFrom(Collection.class)) return ((Collection)o).isEmpty();
            return o.equals(rule);
        }
        return true;
    }



}
