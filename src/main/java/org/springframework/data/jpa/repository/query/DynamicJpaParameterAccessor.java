package org.springframework.data.jpa.repository.query;

import org.hibernate.jpa.TypedParameterValue;
import org.springframework.data.repository.query.Parameters;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.data.repository.query.ParameterAccessor} based on an {@link Parameters} instance. It also
 * offers access to all the values, not just the bindable ones based on a {@link JpaParameters.JpaParameter} instance.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicJpaParameterAccessor extends JpaParametersParameterAccessor {
    private JpaParametersParameterAccessor accessor;
    private Object[] values;

    /**
     * Creates a new {@link JpaParametersParameterAccessor}.
     *
     * @param parameters must not be {@literal null}.
     * @param values     must not be {@literal null}.
     */
    public DynamicJpaParameterAccessor(Parameters<?, ?> parameters, Object[] values) {
        super(parameters, values);
        this.values = values;
    }

    public DynamicJpaParameterAccessor(JpaParametersParameterAccessor accessor) {
        super(accessor.getParameters(), accessor.getParameters().stream()
                .map(accessor::getValue).toArray());
        this.values = accessor.getParameters().stream()
                .map(accessor::getValue).toArray();
        this.accessor = accessor;
    }

    public static DynamicJpaParameterAccessor of(Parameters<?, ?> parameters, Object[] values) {
        return new DynamicJpaParameterAccessor(parameters, values);
    }

    public static DynamicJpaParameterAccessor of(JpaParametersParameterAccessor accessor) {
        return new DynamicJpaParameterAccessor(accessor);
    }

    /**
     * Get parameter values
     *
     * @return values
     */
    @Override
    public Object[] getValues() {
        return values;
    }

    /**
     * Get map param with value
     *
     * @return a map
     */
    public Map<String, Object> getParamModel() {
        if (this.accessor != null)
            return getParamModel(this.accessor);
        return getParamModel(this);
    }

    private Map<String, Object> getParamModel(JpaParametersParameterAccessor accessor) {
        Map<String, Object> result = new HashMap<>();
        Parameters<?, ?> parameters = accessor.getParameters();
        parameters.forEach(parameter -> {
            Object value = accessor.getValue(parameter);
            if (value != null && !(value instanceof TypedParameterValue) && parameter.isBindable()) {
                result.put(parameter.getName().orElse(null), value);
            }
        });
        return result;
    }
}
