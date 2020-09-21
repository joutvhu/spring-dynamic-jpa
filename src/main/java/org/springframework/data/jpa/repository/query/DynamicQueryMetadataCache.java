package org.springframework.data.jpa.repository.query;

import org.springframework.data.util.Lazy;

import javax.persistence.Query;

/**
 * Cache for {@link DynamicQueryMetadataCache}. Optimizes for small cache sizes on a best-effort basis.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicQueryMetadataCache extends QueryParameterSetter.QueryMetadataCache {
    public Query bindAndPrepare(String queryString, Query query, JpaParametersParameterAccessor accessor,
                                Lazy<ParameterBinder> parameterBinder) {
        QueryParameterSetter.QueryMetadata metadata = this.getMetadata(queryString, query);

        // it is ok to reuse the binding contained in the ParameterBinder although we create a new query String because the
        // parameters in the query do not change.
        return parameterBinder.get().bindAndPrepare(query, metadata, accessor);
    }

    public void bind(String queryString, Query query, JpaParametersParameterAccessor accessor,
                     Lazy<ParameterBinder> parameterBinder) {
        QueryParameterSetter.QueryMetadata metadata = this.getMetadata(queryString, query);

        parameterBinder.get().bind(metadata.withQuery(query), accessor, QueryParameterSetter.ErrorHandling.LENIENT);
    }
}
