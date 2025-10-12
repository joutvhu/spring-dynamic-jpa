package org.springframework.data.jpa.repository.query;

import jakarta.persistence.Query;
import org.springframework.data.util.Lazy;

/**
 * Cache for {@link DynamicQueryMetadataCache}. Optimizes for small cache sizes on a best-effort basis.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicQueryMetadataCache extends QueryParameterSetter.QueryMetadataCache {
    /**
     * Binds parameters and prepares the query using the provided accessor and parameter binder.
     *
     * @param queryString the query string
     * @param query the JPA query
     * @param accessor the parameter accessor
     * @param parameterBinder the parameter binder
     * @return the prepared query
     */
    public Query bindAndPrepare(String queryString, Query query, JpaParametersParameterAccessor accessor,
                                Lazy<ParameterBinder> parameterBinder) {
        QueryParameterSetter.QueryMetadata metadata = this.getMetadata(queryString, query);

        // it is ok to reuse the binding contained in the ParameterBinder although we create a new query String because the
        // parameters in the query do not change.
        return parameterBinder.get().bindAndPrepare(query, metadata, accessor);
    }

    /**
     * Binds parameters to the query using the provided accessor and parameter binder.
     *
     * @param queryString the query string
     * @param query the JPA query
     * @param accessor the parameter accessor
     * @param parameterBinder the parameter binder
     */
    public void bind(String queryString, Query query, JpaParametersParameterAccessor accessor,
                     Lazy<ParameterBinder> parameterBinder) {
        QueryParameterSetter.QueryMetadata metadata = this.getMetadata(queryString, query);

        parameterBinder.get().bind(metadata.withQuery(query), accessor, QueryParameterSetter.ErrorHandling.LENIENT);
    }
}
