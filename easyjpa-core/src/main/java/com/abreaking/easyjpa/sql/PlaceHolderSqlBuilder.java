package com.abreaking.easyjpa.sql;

import com.abreaking.easyjpa.dao.EasyJpa;
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
public class PlaceHolderSqlBuilder extends AbstractSqlBuilder{

    private static final Pattern pattern = Pattern.compile("(\\$|#)\\{\\w+}");


    PlaceholderMapper placeholderMapper;

    public PlaceHolderSqlBuilder(PlaceholderMapper placeholderMapper){
        this.placeholderMapper = placeholderMapper;
    }

    @Override
    protected void doVisit(EasyJpa easyJpa, ColumnMatrix columnMatrix) {
        Map<String, Object> argsMap = placeholderMapper.getArgsMap();
        Set<Class> entitySet = placeholderMapper.getEntitySet();
        List<PlaceholderMapper.Fragment> fragments = placeholderMapper.getSqlFragmentList();
        if (fragments.isEmpty()){
            throw new EasyJpaException("no placeholder sql statement specified");
        }

        if (easyJpa != null){
            fillParams(argsMap,easyJpa);
        }
        for (Class entity : entitySet){
            fillParams(argsMap,new EasyJpa(entity));
        }

        //有一种情况，传入的是一条sql
        if (fragments.size()==1){
            PlaceholderMapper.Fragment fragment = fragments.get(0);
            //一条完整的sql就不再进行notStay判断了
            sqlBuildIfStay(columnMatrix,fragment.getFragmentSql(),argsMap,key->true);
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
            sqlBuildIfStay(columnMatrix,fragmentSql,argsMap,key->{
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
    private void sqlBuildIfStay(ColumnMatrix columnMatrix, String placeholderFragmentSql, Map<String, Object> argsMap,Function<String,Boolean> bt){
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
                int type = value==null?Types.NULL : SqlUtil.getSoftSqlType(value.getClass());
                columnMatrix.put(argKey,type,value);
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
            return o == rule || o.equals(rule);
        }
        return true;
    }

    /**
     * 将easyJpa里的部分映射内容，填充到params里面(params里不存在的)
     * 也应该可以直接字段名的补充的。后续再实现把
     * @param params
     * @param easyJpa
     */
    public void fillParams(Map<String,Object> params,EasyJpa easyJpa){
        params.putIfAbsent("tableName",easyJpa.getTableName());
        params.putIfAbsent("tablename".toLowerCase(),easyJpa.getTableName());
        params.put(easyJpa.getObj().getSimpleName(),easyJpa.getTableName()); //类名也可以直接替换成表名
        params.put("idName",easyJpa.getIdName());
    }

}
