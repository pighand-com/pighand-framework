# pighand-framework-spring

基于spring boot、mybatis、mybatis-plus。快速开发框架。即插即用，无代码入侵，简化开发流程，帮助developer快速、规范开发。

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

### ResultData（格式化返回值）

使用：\
方法体返回值，推荐带泛型，swagger可以根据泛型自动生成返回值信息

```
public ResultData<PageOrList<VO>> page(VO vo) {
   PageOrList result = service.query(vo);
   return new ResultData<>(result);
}

response:
{
   code: "200",
   error: "",
   data: Object
}
```

#### 返回成功

new Result();\
new Result(returnObject);\
new Result().success();\
new Result().success(returnObject); # 返回成功信息\

#### 返回提示

new Result().prompt(error);

#### 返回异常

new Result().exception(error);