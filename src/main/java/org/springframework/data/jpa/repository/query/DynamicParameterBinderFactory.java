package org.springframework.data.jpa.repository.query;

import org.springframework.core.env.StandardEnvironment;
import org.springframework.data.expression.ValueEvaluationContext;
import org.springframework.data.expression.ValueEvaluationContextProvider;
import org.springframework.data.expression.ValueExpressionParser;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Factory for differently configured {@link ParameterBinder}.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicParameterBinderFactory extends ParameterBinderFactory {
    // Standard environment for expression evaluation
    private static final StandardEnvironment ENVIRONMENT = new StandardEnvironment();
    
    /**
     * Creates a query-aware ParameterBinder for dynamic queries.
     *
     * @param parameters the JPA parameters
     * @param query the declared query
     * @param parser the SpEL expression parser
     * @param evaluationContextProvider the evaluation context provider
     * @return a ParameterBinder instance
     */
    public static ParameterBinder createQueryAwareBinder(
            JpaParameters parameters, DeclaredQuery query, SpelExpressionParser parser,
            QueryMethodEvaluationContextProvider evaluationContextProvider) {
        
        // Adapter to bridge QueryMethodEvaluationContextProvider to ValueEvaluationContextProvider
        ValueEvaluationContextProvider valueContextProvider = new ValueEvaluationContextProvider() {
            @Override
            public ValueEvaluationContext getEvaluationContext(Object rootObject) {
                EvaluationContext context = evaluationContextProvider.getEvaluationContext(parameters, (Object[]) rootObject);
                return ValueEvaluationContext.of(ENVIRONMENT, context);
            }
            
            @Override
            public ValueEvaluationContext getEvaluationContext(Object rootObject, ExpressionDependencies dependencies) {
                EvaluationContext context = evaluationContextProvider.getEvaluationContext(parameters, (Object[]) rootObject, dependencies);
                return ValueEvaluationContext.of(ENVIRONMENT, context);
            }
        };
        
        return ParameterBinderFactory.createQueryAwareBinder(parameters, query, 
                ValueExpressionParser.create(), valueContextProvider);
    }
}
