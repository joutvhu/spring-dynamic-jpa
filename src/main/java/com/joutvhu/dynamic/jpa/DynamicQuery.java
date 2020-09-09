package com.joutvhu.dynamic.jpa;

import org.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@QueryAnnotation
@Documented
public @interface DynamicQuery {
    /**
     * Defines the JPA query to be executed when the annotated method is called.
     */
    String value() default "";

    /**
     * Defines a special count query that shall be used for pagination queries to lookup the total number of elements for
     * a page. If none is configured we will derive the count query from the original query or {@link #countProjection()} query if any.
     */
    String countQuery() default "";

    /**
     * Defines the projection part of the count query that is generated for pagination. If neither {@link #countQuery()}
     * nor {@link #countProjection()} is configured we will derive the count query from the original query.
     *
     * @return
     * @since 1.6
     */
    String countProjection() default "";

    /**
     * Configures whether the given query is a native one. Defaults to {@literal false}.
     */
    boolean nativeQuery() default false;
}
