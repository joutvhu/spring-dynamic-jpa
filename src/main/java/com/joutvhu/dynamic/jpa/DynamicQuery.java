package com.joutvhu.dynamic.jpa;

import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.jpa.repository.QueryRewriter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare finder dynamic queries directly on repository methods.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@QueryAnnotation
@Documented
public @interface DynamicQuery {
    /**
     * Provides a query template method name, which is used to find external query templates.
     * The default is {@code entityName:methodName}, entityName is entity class name, methodName is query method name.
     *
     * @return the query template method name
     * @since x.x.8
     */
    String name() default "";

    /**
     * Defines the JPA query template to be executed when the annotated method is called.
     *
     * @return the JPA query template
     */
    String value() default "";

    /**
     * Defines a special count query template that shall be used for pagination queries to lookup the total number
     * of elements for  a page.
     *
     * @return the count query template
     */
    String countQuery() default "";

    /**
     * Defines the projection part of the count query template that is generated for pagination.
     *
     * @return the count query projection template
     */
    String countProjection() default "";

    /**
     * Configures whether the given query is a native one. Defaults to {@literal false}.
     *
     * @return true if the query is native
     */
    boolean nativeQuery() default false;

    /**
     * Define a {@link QueryRewriter} that should be applied to the query string after the query is fully assembled.
     *
     * @return a {@link QueryRewriter}
     * @since 3.0
     */
    Class<? extends QueryRewriter> queryRewriter() default QueryRewriter.IdentityQueryRewriter.class;
}
