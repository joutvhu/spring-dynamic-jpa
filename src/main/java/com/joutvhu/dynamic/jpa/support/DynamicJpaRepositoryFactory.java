package com.joutvhu.dynamic.jpa.support;

import com.joutvhu.dynamic.jpa.query.DynamicJpaQueryLookupStrategy;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

import javax.persistence.EntityManager;
import java.util.Optional;

/**
 * JPA specific generic repository factory.
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public class DynamicJpaRepositoryFactory extends JpaRepositoryFactory {
    private final EntityManager entityManager;
    private final QueryExtractor extractor;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    /**
     * Creates a new {@link DynamicJpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public DynamicJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.extractor = PersistenceProvider.fromEntityManager(entityManager);
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);
        this.escapeCharacter = escapeCharacter;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(DynamicJpaQueryLookupStrategy
                .create(entityManager, key, extractor, evaluationContextProvider, escapeCharacter));
    }
}
