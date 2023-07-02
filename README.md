# pighand-framework-spring

基于spring boot、mybatis、mybatis-plus。快速开发框架。即插即用，无代码入侵，简化开发流程，帮助developer快速、规范开发。

<!-- TOC -->

* [pighand-framework-spring](#pighand-framework-spring)
    * [快速开始](#快速开始)
        * [安装](#安装)
            * [MAVEN](#maven)
            * [GRADLE](#gradle)
            * [parent](#parent)
        * [配置](#配置)
    * [restful api支持](#restful-api支持)
    * [分页](#分页)
        * [page模式](#page模式)
        * [page_token模式（推荐，前提是表中存在有序、有索引的字段）](#pagetoken模式推荐前提是表中存在有序有索引的字段)
        * [BaseDomain](#basedomain)
        * [PageOrList](#pageorlist)
    * [异常处理](#异常处理)
        * [异常](#异常)
            * [配置（非必须）](#配置非必须)
            * [使用](#使用)
        * [提示](#提示)
            * [使用](#使用-1)
    * [Result（格式化返回值）](#result格式化返回值)
        * [HTTP Result](#http-result)
            * [返回成功](#返回成功)
            * [返回提示](#返回提示)
            * [返回异常](#返回异常)
        * [GRPC Result（格式化返回值）](#grpc-result格式化返回值)
    * [@HttpExchange注册器](#httpexchange注册器)
    * [mysql数据类型转换](#mysql数据类型转换)
    * [工具类](#工具类)
        * [校验工具类 - VerifyUtils](#校验工具类---verifyutils)

<!-- TOC -->

### 快速开始

#### 安装

##### MAVEN

```
<dependencies>
    <dependency>
    	<groupId>com.pighand</groupId>
    	<artifactId>pighand-framework-spring</artifactId>
    	<version>1.1.0</version>
    </dependency>
</dependencies>
```

##### GRADLE

```
dependencies {
        compile 'com.pighand:pighand-framework-spring:1.1.0'
}
```

##### parent

使用[pighand-framework-spring-parent](https://github.com/pighand-com/pighand-framework-spring-parent)，不需要引用spring
boot、pighand-framework。

```
<parent>
    <groupId>com.pighand</groupId>
    <artifactId>pighand-framework-spring-parent</artifactId>
    <version>1.1.0</version>
</parent>
```

#### 配置

1. spring boot Application类，增加注解（必须）
   `@EnableConfigurationProperties(PighandFrameworkConfig.class)`

2. controller继承（非必须）\
   `BaseController<service class>`

3. service继承（非必须）\
   `BaseService<domain class>`

4. serviceImpl继承（非必须）\
   `BaseServiceImpl<mapper class, domain class>`

5. domain继承（非必须）\
   `BaseDomain`

6. mapper继承（非必须）\
   `BaseMapper`

### restful api支持

[pighand-framework-spring-api](https://github.com/pighand-com/pighand-framework-spring-api)

### 分页

1. Application增加拦截器

```
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
  MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
  interceptor.addInnerInterceptor(new PageInterceptor(DbType.MYSQL));
  return interceptor;
}
```

2. spring配置（非必须）

```
pighand:
  page:
    next-column: ""   #page_token模式，下页查询列；默认"id"
    secret-key: ""     #page_token模式，加密key；默认"0__0PIGHAND_____"
    secret-algorithm: ""   #page_token模式，加密算法，默认"AES"
```

3. 分页查询

```
Mapper：
   PageOrList<VO> query(Page pageInfo, VO vo);
   
Service：
   PageOrList pageInfo = vo.pageParamOrInit(PageType.NEXT_TOKEN);
   PageOrList<VO> result = super.mapper.query(pageInfo, vo);
   
Controller：
   @Get
   public ResultData page(VO vo) {}
   
   前端根据分页模式传：pageSize、pageCurrent、pageToken
```

分页支持2中模式

#### page模式

参数：

- pageSize：每页数据量
- pageCurrent：当前页数

返回值：

- total: 总数
- size: 每页数据量
- current: 当前页数
- pages：总页数

#### page_token模式（推荐，前提是表中存在有序、有索引的字段）

不计算count； 可以解决查询下页前，新插入数据的问题。

参数：

- pageSize：每页数据量
- pageToken：查询下一页的token，不传查首页

返回值：

- pageToken：下页查询所用的token，null表示无下页数据

#### BaseDomain

bean继承BaseDomain，bean作为controller接收参数，可直接获取分页参数

- pageParamOrInit(): 获取并初始化分页信息，默认current=1，size=10
- pageParamOrInit(pageType): 获取并初始化分页信息，并设置分页类型
- pageParam(): 获取分页信息，如果前端没传参数，则只查询list
- pageParam(pageType): 获取分页信息并设置分页类型

#### PageOrList

分页查询，用来传分页参数；也用来当做分页查询的结果。

- setNextColumn(String): page_token模式，设置查询列（有序、有索引的字段）
- toList(): 获取返回结果的list

### 异常处理

#### 异常

##### 配置（非必须）

```
pighand:
  exception:
    intercept-exception: false   #拦截Exception
    message: ""   #自定义错误信息，使用throwException时，接口返回的信息始终是在此设置的信息
    responseOk: true     #抛出异常，http状态始终是200
    promptStack: false   #使用throwPrompt时，是否输入日志
```

##### 使用

response:\
code默认500；如果设置pighand.exception.message，error始终是此值。

```
{
   code: "500",
   error: "message",
   data: Object or null
}
```

throw new throwException(errorMessage);\
throw new throwException(errorMessage, data);\
throw new throwException(errorMessage, code);\
throw new throwException(errorMessage, code, data);

#### 提示

##### 使用

response:\
code默认400

```
{
   code: "400",
   error: "message",
   data: Object or null
}
```

throw new throwPrompt(errorMessage);\
throw new throwPrompt(errorMessage, data);\
throw new throwPrompt(errorMessage, code);\
throw new throwPrompt(errorMessage, code, data);

### Result（格式化返回值）

#### HTTP Result

使用：\
方法体返回值，推荐带泛型，swagger可以根据泛型自动生成返回值信息

```
public Result<PageOrList<VO>> page(VO vo) {
   PageOrList result = service.query(vo);
   return new Result<>(result);
}

response:
{
   code: "200",
   error: "",
   data: Object
}
```

##### 返回成功

new Result();\
new Result(returnObject);\
new Result().success();\
new Result().success(returnObject); # 返回成功信息\

##### 返回提示

new Result().prompt(error);

##### 返回异常

new Result().exception(error);

#### GRPC Result（格式化返回值）

```java
@Component public class XxxService extends XxxServiceGrpc.XxxServiceImplBase {

    @Override public void content(ContentRequest request, StreamObserver<XxxResponse> responseObserver) {
        new GrpcResult<>(responseObserver, XxxResponse.newBuilder(),
            // service
            () -> {
                // do something
            },

            // service running success
            (responseBuilder, result, code) -> {
                responseBuilder.setCode(code);
                responseBuilder.setData(result);
            },

            // exception
            (responseBuilder, result, code, message) -> {
                responseBuilder.setCode(code);
                responseBuilder.setError(message);
            });
    }
}
```

### @HttpExchange注册器

在Application中使用`@Import({HttpExchangeRegister.class})`注解，可以自动注册HttpExchange。

<b>*</b> 代码中注入类，需要使用一下方式注入：

1. @Autowired(required = false)
2. @Resource
3. 构造方法注入

### mysql数据类型转换

1. json array to List

```java
// bean class add
@TableName(autoResultMap = true)

// file add
@TableField(typeHandler = ListTypeHandler.class)

// application.yml add
mybatis-plus:
    type-handlers-package:com.pighand.framework.spring.type.handler
```

### 工具类

#### 校验工具类 - VerifyUtils

- isEmpty(Object): 判断对象是否为空 对象为null，字符序列长度为0，集合类、Map为empty
- isIp4(String): 判断是否是IPv4
- verifyMail(String): 判断对象是邮箱
- validateParams(Object): hibernate校验，校验失败抛出异常
- validateParams(Object, ...group): hibernate分组校验，校验失败抛出异常