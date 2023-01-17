package com.joutvhu.dynamic.jpa.query;

import com.joutvhu.dynamic.commons.DynamicQueryTemplate;
import com.joutvhu.dynamic.jpa.DynamicQuery;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.DynamicBasedStringQuery;
import org.springframework.data.jpa.repository.query.DynamicJpaParameterAccessor;
import org.springframework.data.jpa.repository.query.DynamicParameterBinderFactory;
import org.springframework.data.jpa.repository.query.DynamicQueryMetadataCache;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.ParameterBinder;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.util.Lazy;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.Map;
import java.util.Optional;

/**
 * {@link RepositoryQuery} implementation that inspects a {@link DynamicJpaQueryMethod}
 * for the existence of an {@link DynamicQuery} annotation and creates a JPA {@link DynamicQuery} from it.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicJpaRepositoryQuery extends AbstractJpaQuery {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private final DynamicJpaQueryMethod method;
    private final QueryMethodEvaluationContextProvider evaluationContextProvider;
    private final DynamicQueryMetadataCache metadataCache = new DynamicQueryMetadataCache();

    private Object[] values;
    private DynamicBasedStringQuery query;
    private DynamicBasedStringQuery countQuery;
    private Lazy<ParameterBinder> parameterBinder;

    /**
     * Creates a new {@link DynamicJpaRepositoryQuery} from the given {@link AbstractJpaQuery}.
     *
     * @param method                    DynamicJpaQueryMethod
     * @param em                        EntityManager
     * @param evaluationContextProvider QueryMethodEvaluationContextProvider
     */
    public DynamicJpaRepositoryQuery(DynamicJpaQueryMethod method, EntityManager em,
                                     QueryMethodEvaluationContextProvider evaluationContextProvider) {
        super(method, em);

        Assert.notNull(evaluationContextProvider, "ExpressionEvaluationContextProvider must not be null");

        this.method = method;
        this.evaluationContextProvider = evaluationContextProvider;
    }

    protected String buildQuery(DynamicQueryTemplate template, DynamicJpaParameterAccessor accessor) {
        try {
            if (template != null) {
                Map<String, Object> model = accessor.getParamModel();
                String queryString = template.process(model)
                        .replaceAll("\n", " ")
                        .replaceAll("\t", " ")
                        .replaceAll(" +", " ")
                        .trim();
                return queryString.isEmpty() ? null : queryString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected ParameterBinder createBinder() {
        return DynamicParameterBinderFactory.createQueryAwareBinder(getQueryMethod().getParameters(), query, PARSER,
                evaluationContextProvider);
    }

    protected DynamicBasedStringQuery setAccessor(DynamicJpaParameterAccessor accessor, Object[] values) {
        if (query == null || this.values != values) {
            this.values = values;
            String queryString = buildQuery(method.getQueryTemplate(), accessor);
            query = new DynamicBasedStringQuery(queryString, method.getEntityInformation(), PARSER);
            parameterBinder = Lazy.of(createBinder());

            validateQuery();
        }
        return query;
    }

    @Override
    protected Query doCreateQuery(Object[] values) {
        DynamicJpaParameterAccessor accessor = DynamicJpaParameterAccessor.of(getQueryMethod().getParameters(), values);
        setAccessor(accessor, values);

        String sortedQueryString = QueryUtils
                .applySorting(query.getQueryString(), accessor.getSort(), query.getAlias());
        ResultProcessor processor = getQueryMethod().getResultProcessor().withDynamicProjection(accessor);

        Query query = createJpaQuery(sortedQueryString, processor.getReturnedType());

        return metadataCache.bindAndPrepare(sortedQueryString, query, accessor, parameterBinder);
    }

    @Override
    protected Query doCreateCountQuery(Object[] values) {
        DynamicJpaParameterAccessor accessor = DynamicJpaParameterAccessor.of(getQueryMethod().getParameters(), values);
        setAccessor(accessor, values);

        String countQueryString = buildQuery(method.getCountQueryTemplate(), accessor);
        String countProjectionString = buildQuery(method.getCountProjectionTemplate(), accessor);

        countQuery = new DynamicBasedStringQuery(
                query.deriveCountQuery(countQueryString, countProjectionString),
                method.getEntityInformation(), PARSER);

        if (!method.isNativeQuery() && method.isPageQuery())
            validateQuery(countQuery.getQueryString(), "Count query validation failed for method %s!");

        String queryString = countQuery.getQueryString();
        EntityManager em = getEntityManager();

        Query query = method.isNativeQuery() ? em.createNativeQuery(queryString) :
                em.createQuery(queryString, Long.class);

        metadataCache.bind(queryString, query, accessor, parameterBinder);

        return query;
    }

    /**
     * Creates an appropriate JPA query from an {@link EntityManager} according to the current {@link DynamicJpaRepositoryQuery}
     * type.
     *
     * @param queryString  is query
     * @param returnedType of method
     * @return a {@link Query}
     */
    protected Query createJpaQuery(String queryString, ReturnedType returnedType) {
        EntityManager em = getEntityManager();
        if (method.isNativeQuery()) {
            Class<?> type = getTypeToQueryFor(returnedType);
            return type == null ? em.createNativeQuery(queryString) : em.createNativeQuery(queryString, type);
        } else {
            if (this.query.hasConstructorExpression() || this.query.isDefaultProjection())
                return em.createQuery(queryString);
            Optional<Class<?>> typeToRead = getTypeToRead(returnedType);
            return typeToRead.isPresent() ? em.createQuery(queryString, typeToRead.get()) : em.createQuery(queryString);
        }
    }

    @Nullable
    private Class<?> getTypeToQueryFor(ReturnedType returnedType) {
        Class<?> result = getQueryMethod().isQueryForEntity() ? returnedType.getDomainType() : null;
        if (this.query.hasConstructorExpression() || this.query.isDefaultProjection()) return result;
        return returnedType.isProjecting() && !getMetamodel()
                .isJpaManaged(returnedType.getReturnedType()) ? Tuple.class : result;
    }

    private void validateQuery() {
        if (method.isNativeQuery()) {
            Parameters<?, ?> parameters = method.getParameters();

            if (parameters.hasSortParameter() && !query.getQueryString().contains("#sort")) {
                throw new InvalidJpaQueryMethodException(String
                        .format("Cannot use native queries with dynamic sorting in method %s", method));
            }
        } else validateQuery(query.getQueryString(), "Validation failed for query for method %s!");
    }

    /**
     * Validates the given query for syntactical correctness.
     */
    private void validateQuery(String query, String errorMessage) {
        EntityManager validatingEm = null;
        if (getQueryMethod().isProcedureQuery()) return;

        try {
            validatingEm = getEntityManager().getEntityManagerFactory().createEntityManager();
            validatingEm.createQuery(query);

        } catch (RuntimeException e) {
            // Needed as there's ambiguities in how an invalid query string shall be expressed by the persistence provider
            // https://java.net/projects/jpa-spec/lists/jsr338-experts/archive/2012-07/message/17
            throw new IllegalArgumentException(String.format(errorMessage, method), e);
        } finally {
            if (validatingEm != null) {
                validatingEm.close();
            }
        }
    }
}
