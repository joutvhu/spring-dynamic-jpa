package org.springframework.data.jpa.repository.query;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Extension of {@link StringQuery} that evaluates the given query string as a SpEL template-expression.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicBasedStringQuery extends ExpressionBasedStringQuery {
    private final QueryEnhancer queryEnhancer;

    /**
     * Creates a new {@link DynamicBasedStringQuery} for the given query and {@link org.springframework.data.repository.core.EntityMetadata}.
     *
     * @param query       must not be {@literal null} or empty.
     * @param metadata    must not be {@literal null}.
     * @param parser      must not be {@literal null}.
     * @param nativeQuery must not be {@literal null}.
     */
    public DynamicBasedStringQuery(String query, JpaEntityMetadata<?> metadata, SpelExpressionParser parser, boolean nativeQuery) {
        super(query, metadata, parser, nativeQuery);
        this.queryEnhancer = QueryEnhancerFactory.forQuery(this);
    }

    public DynamicBasedStringQuery(DeclaredQuery query, JpaEntityMetadata<?> metadata, SpelExpressionParser parser) {
        this(query.getQueryString(), metadata, parser, false);
    }

    public DynamicBasedStringQuery(DeclaredQuery query, JpaEntityMetadata<?> metadata, SpelExpressionParser parser, boolean nativeQuery) {
        this(query.getQueryString(), metadata, parser, nativeQuery);
    }

    public String deriveCountQueryString(@Nullable String countQuery, @Nullable String countQueryProjection) {
        return StringUtils.hasText(countQuery) ? countQuery : this.queryEnhancer
            .createCountQueryFor(countQueryProjection);
    }
}
