package com.joutvhu.dynamic.jpa.support;

import com.joutvhu.dynamic.jpa.query.DynamicJpaQueryLookupStrategy;
import jakarta.persistence.EntityManager;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.QueryRewriterProvider;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * JPA specific generic repository factory.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicJpaRepositoryFactory extends JpaRepositoryFactory {
    private final EntityManager entityManager;
    private final QueryExtractor extractor;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
    private QueryRewriterProvider queryRewriterProvider;

    /**
     * Creates a new {@link DynamicJpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public DynamicJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.extractor = PersistenceProvider.fromEntityManager(entityManager);
        this.queryRewriterProvider = QueryRewriterProvider.simple();
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);
        this.escapeCharacter = escapeCharacter;
    }

    @Override
    public void setQueryRewriterProvider(QueryRewriterProvider queryRewriterProvider) {
        super.setQueryRewriterProvider(queryRewriterProvider);
        this.queryRewriterProvider = queryRewriterProvider;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(DynamicJpaQueryLookupStrategy
                .create(entityManager, key, extractor, evaluationContextProvider, queryRewriterProvider, escapeCharacter));
    }
}
