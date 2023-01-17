package com.joutvhu.dynamic.jpa.support;

import com.joutvhu.dynamic.jpa.query.DynamicJpaQueryLookupStrategy;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

import javax.persistence.EntityManager;
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
    private JpaQueryMethodFactory queryMethodFactory;

    /**
     * Creates a new {@link DynamicJpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public DynamicJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.extractor = PersistenceProvider.fromEntityManager(entityManager);
        this.queryMethodFactory = new DefaultJpaQueryMethodFactory(extractor);
        super.setQueryMethodFactory(queryMethodFactory);
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);
        this.escapeCharacter = escapeCharacter;
    }

    public void setQueryMethodFactory(JpaQueryMethodFactory queryMethodFactory) {
        super.setQueryMethodFactory(queryMethodFactory);
        this.queryMethodFactory = queryMethodFactory;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
            QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(DynamicJpaQueryLookupStrategy
                .create(entityManager, queryMethodFactory, key, extractor, evaluationContextProvider,
                        escapeCharacter));
    }
}
