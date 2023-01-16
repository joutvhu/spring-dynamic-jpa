package com.joutvhu.dynamic.jpa;

import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.jpa.repository.QueryRewriter;

import java.lang.annotation.*;

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
     * @since 3.0
     */
    Class<? extends QueryRewriter> queryRewriter() default QueryRewriter.IdentityQueryRewriter.class;
}
