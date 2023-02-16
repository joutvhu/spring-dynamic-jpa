# Spring Dynamic JPA

The Spring Dynamic JPA will make it easy to implement dynamic queries with JpaRepository.

## How to use?

### Install dependency

```groovy
implementation 'com.github.joutvhu:spring-dynamic-jpa:3.0.7'
```

```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>spring-dynamic-jpa</artifactId>
    <version>3.0.7</version>
</dependency>
```

- Please choose the _Spring Dynamic JPA_ version appropriate with your spring version.

  | Spring Boot version | Spring Dynamic JPA version |
  |:----------:|:-------------:|
  | 2.0.x.RELEASE | 2.0.7 |
  | 2.1.x.RELEASE | 2.1.7 |
  | 2.2.x.RELEASE | 2.2.7 |
  | 2.3.x.RELEASE | 2.3.7 |
  | 2.4.x | 2.3.7 |
  | 2.5.x | 2.3.7 |
  | 2.6.x | 2.3.7 |
  | 2.7.x | 2.7.7 |
  | 3.0.x | 3.0.7 |

Also, you have to choose a [Dynamic Query Template Provider](https://github.com/joutvhu/spring-dynamic-commons#dynamic-query-template-provider) to use,
the Dynamic Query Template Provider will decide the style you write dynamic query template.

In this document, I will use [Spring Dynamic Freemarker](https://github.com/joutvhu/spring-dynamic-freemarker).
If you migrated from a lower version, you should use it.

```groovy
implementation 'com.github.joutvhu:spring-dynamic-freemarker:1.0.0'
```

```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>spring-dynamic-freemarker</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Configuration

- First you need to create a bean of `DynamicQueryTemplateProvider`, that depending on which the Dynamic Query Template Provider you are using.

```java
@Bean
public DynamicQueryTemplateProvider dynamicQueryTemplateProvider() {
    FreemarkerQueryTemplateProvider provider = new FreemarkerQueryTemplateProvider();
    provider.setTemplateLocation("classpath:/query");
    provider.setSuffix(".dsql");
    return provider;
}
```

- Next, you need to set the jpa repository's `repositoryFactoryBeanClass` property to `DynamicJpaRepositoryFactoryBean.class`.

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

- If you do not specify the query template on the `@DynamicQuery` annotation.
  The `DynamicQueryTemplateProvider` will find them from external template files based on the `TemplateLocation` and `Suffix` that you specify in the provider.

- If you don't want to load the template from external template files you can use the following code `provider.setSuffix(null);`.

- Each template will start with a template name definition line. The template name definition line must be start with two dash characters (`--`). The template name will have the following syntax.
  
  ```
  entityName:methodName[.queryType]
  ```

  - `entityName` is entity class name
  
  - `methodName` is query method name
  
  - `queryType`  corresponds to what query type of `@DynamicQuery` annotation.
    
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

- Now you don't need to specify the query template on `@DynamicQuery` annotation.

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
