package com.joutvhu.dynamic.commons.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.joutvhu.dynamic.commons.util.DynamicTemplateResolver;
import com.joutvhu.dynamic.jpa.DynamicQueryTemplate;
import com.joutvhu.dynamic.jpa.DynamicQueryTemplateHandler;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HandlebarsDynamicQueryTemplateHandler
 *
 * @author Jean Sossmeier
 * @since 2.0.0
 */
@NoArgsConstructor
@Component
public class HandlebarsDynamicQueryTemplateHandler implements
        DynamicQueryTemplateHandler<Template>,
        ResourceLoaderAware,
        InitializingBean {

    private static final Log log = LogFactory.getLog(HandlebarsDynamicQueryTemplateHandler.class);

    private static final Map<String, DynamicQueryTemplate<Template>> cache = new ConcurrentHashMap<>();
    private static final TemplateLoader sqlTemplateLoader = new ClassPathTemplateLoader();
    private static final Handlebars handlebars = new Handlebars(sqlTemplateLoader);

    private String encoding = "UTF-8";
    private String templateLocation = "classpath:/query";
    private String suffix = ".dsql";
    private ResourceLoader resourceLoader;

    @Override
    public DynamicQueryTemplate<Template> createTemplate(String name, String content) {
        try {
            Template template = handlebars.compile(name);
            HandlebarsDynamicQueryTemplate dynamicTemplate
                    = new HandlebarsDynamicQueryTemplate(template);

            cache.put(name, dynamicTemplate);
            return dynamicTemplate;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DynamicQueryTemplate<Template> findTemplate(String name) {
        return cache.computeIfAbsent(name, template -> this.createTemplate(template, null));
    }

    @Override
    @SneakyThrows
    public String processTemplate(DynamicQueryTemplate<Template> template, Map<String, Object> params) {
        return template.getTemplate().apply(params);
    }

    /**
     * Setup encoding for the process of reading the query template files.
     *
     * @param encoding of query template file, default is "UTF-8"
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Specify the location of the query template files.
     *
     * @param templateLocation is location of the query template files, default is "classpath:/query"
     */
    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    /**
     * Specify filename extension of the query template files.
     *
     * @param suffix is filename extension of the query template files, default is ".dsql"
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Specify {@link ResourceLoader} to load the query template files.
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    @SneakyThrows
    public void afterPropertiesSet() throws Exception {
        sqlTemplateLoader.setPrefix(this.templateLocation);
        sqlTemplateLoader.setSuffix(this.suffix);
        sqlTemplateLoader.setCharset(Charset.forName(this.encoding));

        String pattern;
        if (StringUtils.isNotBlank(templateLocation))
            pattern = templateLocation.contains(suffix) ? templateLocation : templateLocation + "/**/*" + suffix;
        else pattern = "classpath:/**/*" + suffix;

        PathMatchingResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(pattern);

        for (Resource resource : resources) {
            DynamicTemplateResolver.of(resource).encoding(encoding).load((templateName, content) -> {
                TemplateSource src = sqlTemplateLoader.sourceAt(templateName);
                if (src == null) {
                    log.error("Failed loading template: " + templateName);
                }
            });
        }
    }
}
