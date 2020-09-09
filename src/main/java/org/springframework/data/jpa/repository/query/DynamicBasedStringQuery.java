package org.springframework.data.jpa.repository.query;

import org.springframework.expression.spel.standard.SpelExpressionParser;

public class DynamicBasedStringQuery extends ExpressionBasedStringQuery {
    /**
     * Creates a new {@link DynamicBasedStringQuery} for the given query and {@link org.springframework.data.repository.core.EntityMetadata}.
     *
     * @param query    must not be {@literal null} or empty.
     * @param metadata must not be {@literal null}.
     * @param parser   must not be {@literal null}.
     */
    public DynamicBasedStringQuery(String query, JpaEntityMetadata<?> metadata, SpelExpressionParser parser) {
        super(query, metadata, parser);
    }

    public DynamicBasedStringQuery(DeclaredQuery query, JpaEntityMetadata<?> metadata, SpelExpressionParser parser) {
        this(query.getQueryString(), metadata, parser);
    }
}
