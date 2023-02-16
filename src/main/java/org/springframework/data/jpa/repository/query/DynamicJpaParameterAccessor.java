package org.springframework.data.jpa.repository.query;

import org.springframework.data.repository.query.Parameters;

import java.lang.reflect.Method;
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
            if (value != null && parameter.isBindable()) {
                String key = parameter.getName().orElse(String.valueOf(parameter.getIndex()));
                result.put(key, getParameterValue(value));
            }
        });
        return result;
    }

    @SuppressWarnings("java:S1872")
    private Object getParameterValue(Object value) {
        Class<?> valueClass = value.getClass();
        String className = valueClass.getName();
        if ("org.hibernate.query.TypedParameterValue".equals(className) ||
                "org.hibernate.jpa.TypedParameterValue".equals(className)) {
            try {
                Method getValue = valueClass.getMethod("getValue");
                return getValue.invoke(value);
            } catch (Exception e) {
                return value;
            }
        } else {
            return value;
        }
    }
}
