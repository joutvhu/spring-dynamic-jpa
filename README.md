# Spring Dynamic JPA

The Spring Dynamic JPA will make it easy to implement dynamic queries with JpaRepository.

## How to use?

- Add dependency

```groovy
implementation 'com.github.joutvhu:spring-dynamic-jpa:2.7.5'
```

```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>spring-dynamic-jpa</artifactId>
    <version>2.7.5</version>
</dependency>
```

- Please choose the _spring-dynamic-jpa_ version appropriate with your spring version.

| spring-boot version | spring-dynamic-jpa version |
|:----------:|:-------------:|
| 2.0.x.RELEASE | 2.0.5 |
| 2.1.x.RELEASE | 2.1.5 |
| 2.2.x.RELEASE | 2.2.5 |
| 2.3.x.RELEASE | 2.3.5 |
| 2.7.x | 2.7.5 |

- To use the dynamic query, you need to set the jpa repository's `repositoryFactoryBeanClass` property to `DynamicJpaRepositoryFactoryBean.class`.

```java
// Config with annotation
@EnableJpaRepositories(repositoryFactoryBeanClass = DynamicJpaRepositoryFactoryBean.class)
```

```xml
<!-- Config with xml -->
<jpa:repositories repository-factory-bean-class="com.joutvhu.dynamic.jpa.support.DynamicJpaRepositoryFactoryBean"/>
```

### Dynamic query

- Methods annotated with `@DynamicQuery` tells `DynamicJpaQueryLookupStrategy` to know the content of the query is query template. It needs to parse the query template to query string before executing the query.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    @DynamicQuery(
        value = "select t from User t where t.firstName = :firstName\n" +
            "<#if lastName?has_content>\n" +
            "  and t.lastName = :lastName\n" +
            "</#if>"
    )
    List<User> findUserByNames(Long firstName, String lastName);

    @Query(value = "select t from User t where t.firstName = :firstName")
    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    @DynamicQuery(
        value = "select USER_ID from USER\n" +
            "<#if name??>\n" +
            "  where concat(FIRST_NAME, ' ', LAST_NAME) like %:name%\n" +
            "</#if>",
        nativeQuery = true
    )
    List<Long> searchIdsByName(String name);

    @DynamicQuery(
        value = "select t from User t\n" +
            "<#if role??>\n" +
            "  where t.role = :role\n" +
            "</#if>",
        countQuery = "select count(t) from User t\n" +
            "<#if role??>\n" +
            "  where t.role = :role\n" +
            "</#if>"
    )
    Page<User> findByRole(String role, Pageable pageable);
}
```

### Load query template files

- You need to configure a `DynamicQueryTemplateHandler` bean to be loadable external query templates.

```java
@Bean
public DynamicQueryTemplateHandler dynamicQueryTemplateHandler() {
    DynamicQueryTemplateHandler handler = new DynamicQueryTemplateHandler();
    handler.setTemplateLocation("classpath:/query");
    handler.setSuffix(".dsql");
    return handler;
}
```


- Each template will start with a template name definition line. The template name definition line must be start with two dash characters (`--`). The template name will have the following syntax.
  
  ```
  entityName:methodName[.queryType]
  ```

  - `entityName` is entity class name
  
  - `methodName` is query method name
  
  - `queryType`  corresponds to what query type of `@DynamicQuery` annotaion
    
  | queryType | DynamicQuery field |
  |:----------:|:-------------:|
  | empty |  DynamicQuery.value |
  | "count" |  DynamicQuery.countQuery |
  | "projection" |  DynamicQuery.countProjection |

- Query templates (Ex: `resoucers/query/user-query.dsql`) 

```sql
--User:findUserByNames
select t from User t where t.firstName = :firstName
<#if lastName?has_content>
  and t.lastName = :lastName
</#if>

-- User:searchIdsByName
select USER_ID from USER
<#if name??>
  where concat(FIRST_NAME, ' ', LAST_NAME) like %:name%
</#if>

-- User:findByRole
select t from User t
<#if role??>
  where t.role = :role
</#if>

-- User:findByRole.count
select count(t) from User t
<#if role??>
  where t.role = :role
</#if>

-- User:findByGroup
select t from User t
<#if group.name?starts_with("Git")>
  where t.groupId = :#{#group.id}
</#if>
```

- If you don't specify the query template inside the `@DynamicQuery` annotation, `DynamicJpaRepositoryQuery` will find it from the external query files.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    @DynamicQuery
    List<User> findUserByNames(Long firstName, String lastName);

    @Query(value = "select t from User t where t.firstName = :firstName")
    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    @DynamicQuery(nativeQuery = true)
    List<Long> searchIdsByName(String name);

    @DynamicQuery
    Page<User> findByRole(String role, Pageable pageable);

    @DynamicQuery
    List<User> findByGroup(Group group);
}
```

## How to write query template

- This library using [Apache FreeMarker](https://freemarker.apache.org) template engine to write query template. You can refer to [Freemarker Document](https://freemarker.apache.org/docs/index.html) to know more about rules.

- Use [Online FreeMarker Template Tester](https://try.freemarker.apache.org) with `tagSyntax = angleBracket` and `interpolationSyntax = dollar` to test your query template.

- From version 2.x.2, we will have three directives are `<@where>`, `<@set>`, `<@trim>`

  - `@where` directive knows to only insert `WHERE` if there is any content returned by the containing tags. Furthermore, if that content begins or ends with `AND` or `OR`, it knows to strip it off.

  ```sql
  select t from User t
  <@where>
    <#if firstName?has_content>
      and t.firstName = :firstName
    </#if>
    <#if lastName?has_content>
      and t.lastName = :lastName
    </#if>
  </@where>
  ```

  - `@set` directive is like the `@where` directive, it removes the commas if it appears at the begins or ends of the content. Also, it will insert `SET` if the content is not empty.

  ```sql
  update User t
  <@set>
    <#if firstName?has_content>
      t.firstName = :firstName,
    </#if>
    <#if lastName?has_content>
      t.lastName = :lastName,
    </#if>
  </@set>
  where t.userId = :userId
  ```

  - `@trim` directive has four parameters: `prefix`, `prefixOverrides`, `suffix`, `suffixOverrides`.
    
    - `prefix` is the string value that will be inserted at the start of the content if it is not empty.
    
    - `prefixOverrides` are values that will be removed if they are at the start of a content.
    
    - `suffix` is the string value that will be inserted at the end of the content if it is not empty.
    
    - `suffixOverrides` are values that will be removed if they are at the end of a content.
    
  ```sql
  <@trim prefix="where (" prefixOverrides=["and ", "or "] suffix=")" suffixOverrides=[" and", " or"]>
  ...
  </@trim>
  ```

## Template Caching and Concurrency
In order to make sure that using this library in any kind of environment, even highly concurrent ones has no 
undesired effects, Freemarker template caching is disabled by default.

Quoting Freemarker page: https://freemarker.apache.org/docs/pgui_misc_multithreading.html

> In a multithreaded environment Configuration instances, Template instances and data-models should be handled as immutable (read-only) objects. That is, you create and initialize them (for example with set... methods), and then you don't modify them later (e.g. you don't call set...). This allows us to avoid expensive synchronized blocks in a multithreaded environment. Beware with Template instances; when you get a Template instance with Configuration.getTemplate, you may get an instance from the template cache that is already used by other threads, so do not call its set... methods (calling process is of course fine).
> 
> The above restrictions do not apply if you access all objects from the same single thread only.
> 
> It is impossible to modify the data-model object or a shared variable with FTL, unless you put methods (or other objects) into the data-model that do that. We discourage you from writing methods that modify the data-model object or the shared variables. Try to use variables that are stored in the environment object instead (this object is created for a single Template.process call to store the runtime state of processing), so you don't modify data that are possibly used by multiple threads. For more information read: Variables, scopes

In order to enable Freemarker template caching you can just provide the required handler bean with the default configuration or define your own configuration with your specific caching needs:

```java
@Bean
public DynamicQueryTemplateHandler dynamicQueryTemplateHandler() {
    DynamicQueryTemplateHandler handler = new DynamicQueryTemplateHandler();
    handler.setConfiguration(FreemarkerTemplateConfiguration.instanceWithDefault().configuration());
    handler.setTemplateLocation("classpath:/query");
    handler.setSuffix(".dsql");
    return handler;
}
```
