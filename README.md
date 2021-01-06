# EasyJpa是什么

EasyJpa是一个轻量的、全自动对象关系映射（ORM）的Java框架。它将实体类与数据库表建立的映射关系，提供通用的CURD操作方法。使用者可以通过面向对象的方式进行JDBC的操作。

此外，EasyJpa也支持你直接使用类似Mybaits自定义的预处理SQL，并支持自定义的返回结果类型。

它具有以下特点：

1. 开箱即用

参见下面的**快速使用**，仅仅三步，你就可以将easyjpa用到你的工程；各种curd操作更easy了。

2. 可以零配置

easyjpa几乎可以做到零配置，可以不需要额外的配置，就可以直接使用了。（数据源的配置应该在你的原有工程中，故不算easyjpa的配置，见下面的**快速使用**）。

3. 耦合度低

easyjpa不会对你的代码造成任何污染，实体类注解引入的也是javax规范的注解。

4. 返回结果自动类型转换

sql执行的结果，支持自定义返回类型，进而可以帮你你省去了类型转换的烦恼。


如果你是Mybaits或Hibernate的使用者，你可能会疑惑我为什么还要重复造轮子，那么我建议可以先看看这个[为什么还要重复造轮子]()。

# 一些约定

1. 关于命名

一般来讲，数据库表名的命名规范都是`aaa_bbb_ccc`这种下划线格式，那么Java类名则是`AaaBbbCcc`这种驼峰格式。所以，根据类名，我们可以很容易联想到表名，反之亦然；EasyJpa默认就是采用这种的映射方式。如果你的表名跟类名不是这种的格式，那么你在你的类上使用`@javax.persistence.Table`注解即可。

比如：
```java
import javax.persistence.Table;

@Table(name = "test_user")
public class User {}
```

同样的，列名与字段名也一样的遵守`aaa_bbb_ccc/aaaBbbCcc`这种格式，注意字段名开始字母是小写的。如果根据这样的方式，你的列名跟字段名对应不起来，那么在你的字段名上加上`@javax.persistence.Column`即可。

比如：
```java
import javax.persistence.Column;

public class User {
    @Column(name = "user_id")
    private Integer id;
}
```

2. 使用包装类型

目前easyjpa仅支持Java的基本类型，并且应该是包装类型，包括：
```
String、Integer、Long、Float、Double、Date
```
目前不支持外键的类型以及其他类型，会被自动转为String类型。
所以，数据库表字段应尽量是简单类型。

3. 关于主键

每个表都应该有其主键，主键的唯一作用就是标识一行数据。这将在数据的修改、删除、数据回显等地方作用明显，尤其是前后端交互的情况下。


# 快速上手
先进行如下的准备工作：

1. 将easyjpa引入了你的工程

目前可直接把`easyjpa-core`代码clone下来，然后直接copy到你的工程即可。

2. 指定数据源

easyjpa所有的curd操作都是封装在`EasyJpaDao`这个通用的dao组件里的，`EasyJpaDao`默认使用Jdbc原始的sql执行器，一般来说，它应配置成单例模式，比如后续你将看到整合到Spring中。

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

然后可能还会注意到程序里并没有指定`${tableName}`这个参数，但是程序还是执行成功了。这是因为如果你传入的是一个实体类，easyjpa会自动获取到该类对应的表名，然后注入到placeholderSql里。后面你会看到更多细节。

# 为什么还要造轮子？（解决了什么问题）

目前最流行的ORM框架是Mybaits和Hibernate。国内Mybaits比Hibernate更流行一些（听过国外用hibernate较多，其实我个人也觉得hibernate更好用一些）。

Mybaits与Hibernate的优点就不过多说了，毕竟是很成熟的框架。Mybaits使用的几乎是纯sql，并且对sql输入参数以及结果类型封装得比较好；hibernate则是全自动的ORM框架，配合注解开发效率显著。

## Mybaits的主要问题

1. 它是半自动的

这就意味着你做的任何数据库操作，都需要手写一条sql，哪怕是最简单的sql语句。一条insert、update、delete、select或分页查询语句，每有一个实体类（表）都会在mapper映射文件里都需要写一遍，几乎每个实体类都会有这样的操作，开发效率低下。所以，诸如此类的CURD操作应该被通用起来。

现在有很多mybaits工具，可自动生成mapper文件，自动生成增删改查的sql，但这并不是一劳永逸的，万一日后某个表结构发生改变了呢？

2. 工程臃肿

mybaits的sql都是写在`*.mapper.xml`这样的映射文件，基本上一个类一个映射文件。当工程比较大时，数据库表非常多时，你会发现你的工程躺着大量的mapper.xml文件以及mapper接口，这样会显得你的工程特别臃肿。比如：
![](@attachment/Clipboard_2021-01-06-16-07-44.png)

3. 无法快速响应数据库表结构的修改
如果数据库表结构发生改变，比如新加入了一个字段，那么mybaits就需要先修改实体类，然后再修改mapper文件里实体类的映射信息，最后再调整相关sql语句。这是一个非常繁琐的步骤。

此外，mybaits将对象与表的映射信息放在映射文件里，我觉得是最大的一个败笔。配置麻烦，而且个人认为使用主机比使用配置文件效果更好，既然是实体类，那么与数据库表的映射关系就应该绑定在一起。

## Hibernate的主要问题

1. 不易理解其hql机制

hibernate有个hql语句，我觉得几乎只能将其做一些简单的增删改查操作。因为如果写复杂了，我不知道我这条hql是否写对了（写在mybaits里的sql我还能放到数据库上运行一遍，确认没问题，然后我才会放心的放在mapper文件里）。 当然，如果对hibernate很熟悉、很深入，也许hql一般没问题，但这样一来学习的成本就高了。

其实原生sql远比hibernate的hql功能更强大，并且sql也容易维护。

2. 性能比较低下

hibernate的查询会将表中所有字段都查询出来，因为其hql的机制。

当然hibernate也支持直接使用sql，开发模式又不一样了，这样一来就还得需要转换下思维。并且对返回结果类型处理得并不太好，复杂点的sql执行结果返回的是数组对象，类型也不知道，很多时候都得需要手动再处理一次，


## EasyJpa解决了什么问题

EasyJpa借鉴了Mybaits及Hibernate框架的优秀特征，采用全新的设计思路，并几乎完全解决了上述所说的问题。此外，对比这两个框架而言，它主要有下述提升：

* 原理容易理解，使用上手更快

EasyJpa的最初的设计思路可以一句话概括：“对象本身即可视为查询条件”，后来这个范围慢慢扩大了。实体类本身就可以自动与数据库表映射，使用javax规范的注解，就可以干掉你绝大部分映射文件，

EasyJpa的[快速上手](https://github.com/aBreaking/easyjpa/blob/main/README.md#%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B)。几乎不需要额外的配置，简单几步操作即可将EasyJpay应用到业务工程。

* 全自动的ORM框架，完全面向对象的开发模式

正如上面所说EasyJpa的设计思路，所以你可以使用面向对象的模式来进行CRUD操作。比如你可以使用面向对象的思维来组装查询条件，也支持你直接使用预处理的sql，sql里的参数也可以通过面向对象的思维来组织。

* 对返回结果类型封装得更好

如果使用sql查询，EasyJpa支持你自定义返回结果类型，可以是实体类对象；也可以是Map对象，并且Map里value的数据类型也可以自定义；甚至可以返回多个实体类对象。解决你对返回结果类型二次处理的烦恼。


* 通用的DAO，极大简化工程的代码量以及配置文件数量

EasyJpa提供了一个通用的DAO=>`EasyJpaDao`，可以替换你工程大部分的dao方法。通用的增、删、改、条件查询、分页查询方法均可对任意实体类作用，这可以极大的简化你Mybaits的mapper文件。
