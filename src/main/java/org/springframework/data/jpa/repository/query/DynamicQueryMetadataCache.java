package org.springframework.data.jpa.repository.query;

import org.springframework.data.util.Lazy;

import javax.persistence.Query;

import static org.springframework.data.jpa.repository.query.QueryParameterSetter.ErrorHandling.LENIENT;

/**
 * Cache for {@link DynamicQueryMetadataCache}. Optimizes for small cache sizes on a best-effort basis.
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public class DynamicQueryMetadataCache {
    public Query bindAndPrepare(String queryString, Query query, DynamicJpaParameterAccessor accessor,
                                Lazy<ParameterBinder> parameterBinder) {
        return parameterBinder.get().bindAndPrepare(query, accessor.getValues());
    }

    public void bind(String queryString, Query query, DynamicJpaParameterAccessor accessor,
                     Lazy<ParameterBinder> parameterBinder) {
        parameterBinder.get().bind(query, accessor.getValues(), LENIENT);
    }
}
