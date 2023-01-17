package com.joutvhu.dynamic.jpa.query;

import com.joutvhu.dynamic.jpa.DynamicQuery;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.*;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * {@link QueryLookupStrategy} that tries to detect a dynamic query declared via {@link DynamicQuery} annotation.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicJpaQueryLookupStrategy implements QueryLookupStrategy {
    private final EntityManager entityManager;
    private QueryExtractor extractor;
    private QueryLookupStrategy jpaQueryLookupStrategy;
    private QueryMethodEvaluationContextProvider evaluationContextProvider;
    private QueryRewriterProvider queryRewriterProvider;

    public DynamicJpaQueryLookupStrategy(EntityManager entityManager, JpaQueryMethodFactory queryMethodFactory,
                                         @Nullable Key key, QueryExtractor extractor,
                                         QueryMethodEvaluationContextProvider evaluationContextProvider,
                                         QueryRewriterProvider queryRewriterProvider, EscapeCharacter escape) {
        this.jpaQueryLookupStrategy = JpaQueryLookupStrategy.create(entityManager, queryMethodFactory, key,
                evaluationContextProvider, queryRewriterProvider, escape);
        this.extractor = extractor;
        this.entityManager = entityManager;
        this.evaluationContextProvider = evaluationContextProvider;
        this.queryRewriterProvider = queryRewriterProvider;
    }

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        if (isMethodDynamicJpaHandle(method)) {
            DynamicJpaQueryMethod queryMethod = new DynamicJpaQueryMethod(method, metadata, factory, extractor);
            return new DynamicJpaRepositoryQuery(queryMethod, entityManager,
                    queryRewriterProvider.getQueryRewriter(queryMethod), evaluationContextProvider);
        } else return jpaQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
    }

    private boolean isMethodDynamicJpaHandle(Method method) {
        DynamicQuery annotation = method.getAnnotation(DynamicQuery.class);
        return annotation != null;
    }

    public static QueryLookupStrategy create(EntityManager entityManager, JpaQueryMethodFactory queryMethodFactory,
                                             @Nullable Key key, QueryExtractor extractor,
                                             QueryMethodEvaluationContextProvider evaluationContextProvider,
                                             QueryRewriterProvider queryRewriterProvider, EscapeCharacter escape) {
        Assert.notNull(entityManager, "EntityManager must not be null!");
        Assert.notNull(evaluationContextProvider, "EvaluationContextProvider must not be null!");

        return new DynamicJpaQueryLookupStrategy(entityManager, queryMethodFactory, key, extractor,
                evaluationContextProvider, queryRewriterProvider, escape);
    }
}
