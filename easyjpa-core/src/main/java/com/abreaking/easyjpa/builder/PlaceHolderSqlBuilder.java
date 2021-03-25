package com.abreaking.easyjpa.builder;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.builder.prepare.PreparedWrapper;
import com.abreaking.easyjpa.builder.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.exception.EasyJpaException;

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
        List<PlaceholderMapper.Fragment> fragmentList = placeholderMapper.getFragmentList();
        Map<String, Object> argMap = placeholderMapper.getArgMap();
        StringBuilder sqlBuilder = new StringBuilder();
        List<Object> values = new ArrayList<>();
        for (PlaceholderMapper.Fragment fragment : fragmentList){
            Function<Map, Boolean> rule = fragment.getRule();
            if (rule.apply(argMap)){
                doParsePlaceholder(sqlBuilder,values,argMap,fragment.getFragmentSql());
            }
        }
        return new PreparedWrapper(sqlBuilder.toString(),values.toArray());
    }

    private void doParsePlaceholder(StringBuilder sqlBuilder,List<Object> values,Map<String, Object> argMap,String fragmentSql){
        Matcher matcher = pattern.matcher(fragmentSql);
        int i = 0;
        while (matcher.find()){
            String group = matcher.group();
            String argKey = group.substring(2,group.length()-1);
            Object value = argMap.get(argKey);
            if (value==null) throw new EasyJpaException("The placeholder parameter '"+argKey+"' " +
                    "does not specify the corresponding value of the parameter or its value may be NULL. The placeholder sql fragment is："+fragmentSql);
            sqlBuilder.append(fragmentSql, i, matcher.start());
            if (group.startsWith("${")){
                sqlBuilder.append(value);
            }else if (group.startsWith("#{")){
                sqlBuilder.append("?");
                values.add(value);
            }
            i = matcher.end();
        }
        sqlBuilder.append(fragmentSql,i,fragmentSql.length());
        sqlBuilder.append(" ");
    }


}
