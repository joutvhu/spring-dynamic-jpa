# spring-dynamic-jpa

The Spring Dynamic JPA will make it easy to implement dynamic queries with JpaRepository.

## How to use?

- Add dependency

```groovy
implementation 'com.github.joutvhu:spring-dynamic-jpa:1.1.0'
```

```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>spring-dynamic-jpa</artifactId>
    <version>1.1.0</version>
</dependency>
```

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

- Methods annotated with `@DynamicQuery` tells `DynamicJpaQueryLookupStrategy` know the content of the query is query template. It needs to parse the query template to query string before execute the query.

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
            "  and concat(FIRST_NAME, ' ', LAST_NAME) like %:name%\n" +
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

- You need to configure a `DynamicQueryTemplates` bean to be loadable external query templates.

```java
@Bean
public DynamicQueryTemplates dynamicQueryTemplates() {
    DynamicQueryTemplates queryTemplates = new DynamicQueryTemplates();
    queryTemplates.setTemplateLocation("classpath:/query");
    queryTemplates.setSuffix(".dsql");
    return queryTemplates;
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
  and concat(FIRST_NAME, ' ', LAST_NAME) like %:name%
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
}
```

## How to write query template

- This library using [Apache FreeMarker](https://freemarker.apache.org) template engine to write query template. You can refer to [Freemarker Document](https://freemarker.apache.org/docs/index.html) to know more about rules.