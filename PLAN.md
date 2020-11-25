# 第一阶段，针对实体的增删改查全部实现
1. 应该有个封装对象（暂时叫EasyJpa），传入 user这个实体。。。EasyJpa是否有必要扩展：分页的EasyJpa
     EasyJpa easyJpa = new EasyJpa(User.class); EasyJpa easyJpa = new EasyJpa(new User());
2. EasyJpa 里有各种可使用的where条件，比如 like , gt lte,between
     easyJpa.like("name","%jack%");easyJpa.gt("score",10); easyJpa.order("id","desc");
3. 执行easyJpa,增删改查
     查： List<User> userList = easyJpa.selectPage(0,10); List userList= easyJpa.select();
     增: easyJpa.insert();
     改：easyJpa.update();
     增改：easyJpa.save(); //easyJpa must have id or pk 
     删: eastJpa.delete(); //easyJpa must have id or pk
     
# 第二阶段，直接sql语句的直接查询，并对返回结果的封装     
执行一个sql：select u.id , u.name , u.birthday from user where user.id = ?
1. 应该有个sql解析器，比如参考下：com.alibaba.druid.sql.parser.SQLStatementParser
    解析出select 出来的列名
2. sql select 出来的应该是数组result，根据列名->字段名  ，需要传入实体User.class，对照解析
    User user = new User(); user.setId(result.id) ; user.setName(result.name) 