package com.joutvhu.dynamic.jpa.support;

import com.joutvhu.dynamic.jpa.query.DynamicJpaQueryLookupStrategy;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;

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
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, EvaluationContextProvider evaluationContextProvider) {
        return Optional.of(DynamicJpaQueryLookupStrategy
                .create(entityManager, key, extractor, evaluationContextProvider));
    }
}
