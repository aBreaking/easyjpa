# EasyJpa是什么

EasyJpa是一个轻量的、全自的动对象关系映射（ORM）Java框架。它将实体类与数据库表建立的映射关系，提供通用的CRUD操作API，能够自动生成预处理的SQL语句并执行，使用者可以通过面向对象的思维方式来轻松完成增删改查的操作。

它具有以下特点：

1. 开箱即用

EasyJpa上手简单，只要有数据源连接Connection，甚至可以不需要额外的配置，你就可以将EasyJpa轻松应用到你工程的dao层，各种curd操作更easy了。

2. 无侵入性

EasyJpa遵循Java Persistence API规范，使用`javax.persistence`相关的注解，甚至可以不用注解，实现了对象表关系映射，不会对你的POJO实体类代码造成污染。

3. 通用DAO

EasyJpa在dao层提供的增删改查API，均对任意实体类通用。并封装了一些SQL关键字的API，你可以通过面向对象的思维方式，来进行jdbc操作。

4. 支持动态SQL

EasyJpa也支持你直接使用预处理SQL语句或类似Mybaits的预处理的SQL，并对SQL参数类型、返回结果类型自动转换，也支持自定义返回类型。


如果你是Mybaits或Hibernate的使用者，你可能会疑惑我为什么还要重复造轮子？EasyJpa有什么区别？解决了什么问题？那么我建议可以先看看这个：[为什么还要重复造轮子](https://github.com/aBreaking/easyjpa/wiki/%E8%A7%A3%E5%86%B3%E4%BA%86%E4%BB%80%E4%B9%88%E9%97%AE%E9%A2%98%EF%BC%88%E4%B8%BA%E4%BB%80%E4%B9%88%E8%BF%98%E8%A6%81%E9%80%A0%E8%BD%AE%E5%AD%90%EF%BC%89)。

# 一些约定

1. 关于命名

一般来讲，数据库表名的命名规范都是`aaa_bbb_ccc`这种下划线格式，那么Java类名则是`AaaBbbCcc`这种驼峰格式。所以，根据类名，我们可以很容易联想到表名，反之亦然；EasyJpa默认就是采用这种的方式来自动完成表名-类名映射。

如果你的表名跟类名不是这种的格式，那么你在你的类上使用`@javax.persistence.Table`注解即可。比如：
```java
import javax.persistence.Table;

@Table(name = "test_user")
public class User {}
```

同样的，列名与字段名也一样的遵守`aaa_bbb_ccc/aaaBbbCcc`这种格式，注意字段名开始字母是小写的。

不是这种格式，字段名上需要使用`@javax.persistence.Column`，比如：
```java
import javax.persistence.Column;

public class User {
    @Column(name = "user_id")
    private Integer id;
}
```

2. 使用基本类型，并且是包装类型

数据库表字段的类型应该尽可能地是简单的类型（比如数字、字符串、时间）。不建议使用外键！对应的Java实体类字段类型应该是基本类型（包括Date时间类型），并且应该是包装类型（这一点很重要），包括：
```
String、Integer、Long、Float、Double、java.util.Date
```
注意：目前版本EasyJpa不支持外键以及其他类型，返回的结果会被自动转为String类型。所以，数据库表字段应尽量是简单类型。

3. 关于主键

每个表都应该有其主键（建议自然主键），主键的唯一作用就是标识一行数据，这将在数据的修改、删除、数据回显等地方作用明显，尤其是前后端交互的情况下。

EasyJpa默认认为你的主键字段名就直接叫`id`。或者主键可以使用`@javax.persistence.Id`来标识，比如：
```java
import javax.persistence.Id;

public class User {
    @Id
    private Integer userId;
}
```


# 快速上手
先进行如下的准备工作：

1. 将EasyJpa引入了你的工程

目前可直接把`easyjpa-core`代码clone下来，然后直接copy到你的工程即可。

2. 指定数据源

EasyJpa所有的curd操作都是封装在`EasyJpaDao`这个通用的dao组件里的，`EasyJpaDao`默认使用Jdbc原始的sql执行器，一般来说，它应配置成单例模式，比如后续你将看到整合到Spring中。

目前你可以在你的测试类中先使用如下的代码来实例化一个简单的EasyJpaDao。
```java
static Connection localhostConnection()  {
    String jdbcDriver = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://数据库ip:端口/数据库名";
    String jdbcUserName = "你的数据库用户名";
    String jdbcPassword = "你的数据库密码";
    try {
        Class.forName(jdbcDriver);
        Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
        connection.setAutoCommit(true); // update/insert/delete 自动提交
        return connection;
    }catch (Exception e){
        throw new RuntimeException(e);
    }
}

static EasyJpaDao dao = new EasyJpaDaoImpl(localhostConnection());
```

3. 操作对象（表、类）准备

创建你的数据库表以及对象的实体类。

比如万能的User表（MySql数据库）：
```sql
CREATE TABLE `user` (
  `user_id`   int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) DEFAULT NULL,
  `birthday`  datetime DEFAULT NULL,
  `height`    float DEFAULT NULL,
  `phone_no`  bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
)  DEFAULT CHARSET=utf8
```

然后根据这个表定义User类：
```java
public class User {
    @Id
    private Integer userId;
    private String userName;
    private Date birthday;
    private Long phoneNo;
    private Float height;
    // 它至少有一个空参数的构造方法，对象默认就有的。
    // getter setter方法 不能少哦，这里略
}
```

接下来我们就开始User的增删改查简单操作：

## 增（INSERT）
使用EasyJpaDao.insert方法插入一条user数据
```java
@Test
public void test01(){
    User user = new User();
    user.setUserId(1);
    user.setUserName("张三");
    user.setBirthday(new Date());
    user.setHeight(1.81F);
    user.setPhoneNo(1008611L);
    dao.insert(user);
}
```
然后可以在数据库表上看到该条记录。
```
user_id  user_name     birthday             height     phone_no  
-------  ------------  -------------------  ------  -------------
      1  张三            2021-01-05 17:46:12    1.81        1008611
```

是不是很简单。

它其实相当于执行了这样一条sql语句：
```sql
INSERT INTO user(user_name,user_id,phone_no,height,birthday) VALUES(?,?,?,?,?)  ----这个表示预处理的sql语句
[张三, 1, 1008611, 1.81, Tue Jan 05 17:46:12 CST 2021]               ----这个表示预处理sql语句里的参数，下同
```

## 删（DELETE）
EasyJpaDao.deleteById,根据主键删除。
```java
@Test
public void test02(){
    dao.deleteById(User.class,1);
}
```

相当于执行如下的sql:
```sql
DELETE FROM user where user_id = ?
[1]
```

## 改（UPDATE）
先恢复数据，还是user_id=1的那条数据

1. EasyJpaDao.update，根据主键修改

比如将userId=1的数据userName改成"张小三"

```java
@Test
public void test03(){
    User user = new User();
    user.setUserId(1); // 指定主键值
    user.setUserName("张小三"); // 只修改userName这个字段
    dao.update(user);
}
```
此时可看到主键=1的行数据被修改了。

相当于执行了如下sql：
```sql
UPDATE user SET user_name= ? WHERE user_id = ?   
[张小三, 1]
```

2. EasyJpaDao.updateByCondition，根据指定条件进行修改
比如把姓张的birthday改成今天。
```java
@Test
public void test031(){
    User user = new User();
    user.setBirthday(new Date());

    EasyJpa easyJpa = new EasyJpa(User.class);
    easyJpa.and(Condition.like("userName","张"));

    dao.updateByCondition(user,easyJpa);
}
```
相当于：
```sql
UPDATE user SET birthday= ? WHERE user_name LIKE ?   
[Tue Jan 05 17:54:28 CST 2021, %张%]
```

## 查（SELECT）
select 操作应该是数据库最多的操作了吧。easyJpa提供了若干方法来进行条件查询，包括`EasyJpaDao.get`,`EasyJpaDao.query`。

1. EasyJpaDa.get：直接根据主键来查询唯一的一条数据。
```java
@Test
public void test04(){
  User user = dao.get(User.class, 1);
}
```

接下来的条件查询，先insert若干条user数据。

2. EasyJpaDa.query，将对象本身作为条件来进行查询：

```java
@Test
public void test05(){
    User user = new User();
    user.setUserName("zhangsan");
    user.setUserId(1);
    List<User> userList = dao.query(user);
}
```

它最终会被解释成这样的SQL：

```sql
SELECT * FROM user WHERE user_name = ? AND user_id = ?   
[zhangsan, 1]
```

3. EasyJpaDao.queryByCondition，条件查询

比如这条sql：
```sql
SELECT 
  user_name,birthday
FROM
  USER 
WHERE (
    user_name LIKE '%张%' 
    OR user_name LIKE '%李%'
  ) 
  AND user_id BETWEEN 2 AND 6 
  AND birthday > '2020-12-10' 
ORDER BY user_name DESC 
LIMIT 2, 5 
```
使用EasyJpa来描述
```java
@Test
public void test06() throws ParseException {
    EasyJpa easyJpa = new EasyJpa(User.class);
    easyJpa.select("userName","birthday");
    easyJpa.or(Condition.like("userName","张"));
    easyJpa.or(Condition.like("userName","李"));
    easyJpa.and(Condition.to("birthday",">",new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-10")));
    easyJpa.and(Condition.between("user_id",0,8));
    easyJpa.orderBy("userName",false);
    easyJpa.limit(2,5);
    List<User> userList = dao.queryByCondition(easyJpa);
}
```

看到这里，如果有点不好理解，先没关系，或者你可以截止看到这里就可以了。下一章会详细阐述EasyJpa以及Condition，相信你会很容易理解的。

## 直接执行Sql

1. 预处理Sql

前面你其实可以看到，所有的easyJpa的操作，最终都会转换成带`?`这样的预处理sql，所以，你也可以直接写一条预处理的sql手动执行：
```java
@Test
public void test07(){
    String prepareSql = "select user_id,user_name from user where user_name like ? and height>?";
    PreparedMapper preparedMapper = EasyJpa.buildPrepared(prepareSql, "%王%", 1.7F);
    List<User> list = dao.queryByPreparedSql(preparedMapper,User.class);
}
```

2. 占位符Sql

有时，你需要前台接收一些参数，拿到这些参数及对应的值之后，然后才需要组装sql，此时你可以使用带`${}`或`#{}`格式的占位符sql：

```java
@Test
public void test08(){
    String prepareSql = "select user_name,height from ${tableName} where user_name like #{userName} and height>#{height}";
    User user = new User();
    user.setUserName("%王%");
    user.setHeight(1.7F);
    PlaceholderMapper placeholderMapper = EasyJpa.buildPlaceholder(prepareSql, user);
    List<User> list = dao.queryByPlaceholderSql(placeholderMapper,User.class);
}
```
你可以看到，占位符的sql风格很像mybaits里的mapper文件，其实就是借鉴了mybaits的格式。

然后可能还会注意到程序里并没有指定`${tableName}`这个参数，但是程序还是执行成功了。这是因为如果你传入的是一个实体类，EasyJpa会自动获取到该类对应的表名，然后注入到placeholderSql里。后面你会看到更多细节。

