package com.joutvhu.dynamic.jpa.query;

import com.joutvhu.dynamic.commons.DynamicQueryTemplate;
import com.joutvhu.dynamic.commons.DynamicQueryTemplateProvider;
import com.joutvhu.dynamic.commons.util.ApplicationContextHolder;
import com.joutvhu.dynamic.jpa.DynamicQuery;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA specific extension of {@link JpaQueryMethod}.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicJpaQueryMethod extends JpaQueryMethod {
    private static final Map<String, String> templateMap = new HashMap<>();

    private final Method method;
    private final Lazy<Boolean> isNativeQuery;

    private DynamicQueryTemplateProvider queryTemplateProvider;
    private DynamicQueryTemplate queryTemplate;
    private DynamicQueryTemplate countQueryTemplate;
    private DynamicQueryTemplate countProjectionTemplate;

    static {
        templateMap.put("value", "");
        templateMap.put("countQuery", "count");
        templateMap.put("countProjection", "projection");
    }

    /**
     * Creates a {@link JpaQueryMethod}.
     *
     * @param method    must not be {@literal null}
     * @param metadata  must not be {@literal null}
     * @param factory   must not be {@literal null}
     * @param extractor must not be {@literal null}
     */
    protected DynamicJpaQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory, QueryExtractor extractor) {
        super(method, metadata, factory, extractor);
        this.method = method;
        this.isNativeQuery = Lazy
                .of(() -> getMergedOrDefaultAnnotationValue("nativeQuery", DynamicQuery.class, Boolean.class));
    }

    private DynamicQueryTemplateProvider getTemplateProvider() {
        if (queryTemplateProvider == null)
            queryTemplateProvider = ApplicationContextHolder.getBean(DynamicQueryTemplateProvider.class);
        return queryTemplateProvider;
    }

    protected DynamicQueryTemplate findTemplate(String name) {
        DynamicQueryTemplateProvider provider = getTemplateProvider();
        return provider != null ? provider.findTemplate(name) : null;
    }

    protected DynamicQueryTemplate createTemplate(String name, String query) {
        DynamicQueryTemplateProvider provider = getTemplateProvider();
        return provider != null ? provider.createTemplate(name, query) : null;
    }

    protected DynamicQueryTemplate getTemplate(String name) {
        String templateName = templateMap.get(name);
        if (StringUtils.hasText(templateName)) templateName = "." + templateName;
        templateName = getTemplateKey() + templateName;
        String query = getMergedOrDefaultAnnotationValue(name, DynamicQuery.class, String.class);
        queryTemplate = StringUtils.hasText(query) ? createTemplate(templateName, query) : findTemplate(templateName);
        return queryTemplate;
    }

    public boolean isNativeQuery() {
        return this.isNativeQuery.get();
    }

    @Nullable
    public DynamicQueryTemplate getQueryTemplate() {
        if (queryTemplate == null)
            queryTemplate = getTemplate("value");
        return queryTemplate;
    }

    @Nullable
    public DynamicQueryTemplate getCountQueryTemplate() {
        if (countQueryTemplate == null)
            countQueryTemplate = getTemplate("countQuery");
        return countQueryTemplate;
    }

    @Nullable
    public DynamicQueryTemplate getCountProjectionTemplate() {
        if (countProjectionTemplate == null)
            countProjectionTemplate = getTemplate("countProjection");
        return countProjectionTemplate;
    }

    private String getEntityName() {
        return getEntityInformation().getJavaType().getSimpleName();
    }

    private String getTemplateKey() {
        return getEntityName() + ":" + getName();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> T getMergedOrDefaultAnnotationValue(String attribute, Class annotationType, Class<T> targetType) {
        Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
        if (annotation == null)
            return targetType.cast(AnnotationUtils.getDefaultValue(annotationType, attribute));
        return targetType.cast(AnnotationUtils.getValue(annotation, attribute));
    }
}
