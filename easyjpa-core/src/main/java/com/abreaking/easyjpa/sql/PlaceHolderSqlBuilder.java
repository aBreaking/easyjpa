package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.condition.Conditions;
import com.abreaking.easyjpa.dao.prepare.PreparedWrapper;
import com.abreaking.easyjpa.dao.prepare.PlaceholderMapper;
import com.abreaking.easyjpa.exception.EasyJpaException;

import java.util.*;
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
        StringBuilder placeholderSqlBuilder = placeholderMapper.getPlaceholderSqlBuilder();
        Map<String, Object> argMap = placeholderMapper.getArgMap();
        StringBuilder sqlBuilder = new StringBuilder();
        List<Object> values = new ArrayList<>();
        Matcher matcher = pattern.matcher(placeholderSqlBuilder);
        int i = 0;
        while (matcher.find()){
            String group = matcher.group();
            String argKey = group.substring(2,group.length()-1);
            Object value = argMap.get(argKey); // 这里value可能为空值
            sqlBuilder.append(placeholderSqlBuilder, i, matcher.start());
            if (group.startsWith("${")){
                if (value==null) throw new EasyJpaException("The placeholder parameter '"+argKey+"' does not specify the corresponding value of the parameter or its value may be NULL. The placeholder sql is："+placeholderSqlBuilder);
                sqlBuilder.append(value);
            }else{
                sqlBuilder.append("?");
                values.add(value);
            }
            i = matcher.end();
        }
        sqlBuilder.append(placeholderSqlBuilder,i,placeholderSqlBuilder.length());
        sqlBuilder.append(" ");
        return new PreparedWrapper(sqlBuilder.toString(),values.toArray());
    }


}
