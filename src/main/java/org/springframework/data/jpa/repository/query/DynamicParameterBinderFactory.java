package org.springframework.data.jpa.repository.query;

import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Factory for differently configured {@link ParameterBinder}.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicParameterBinderFactory extends ParameterBinderFactory {
    public static ParameterBinder createQueryAwareBinder(
            JpaParameters parameters, DeclaredQuery query, SpelExpressionParser parser,
            EvaluationContextProvider evaluationContextProvider) {
        return ParameterBinderFactory.createQueryAwareBinder(parameters, query, parser, evaluationContextProvider);
    }
}
