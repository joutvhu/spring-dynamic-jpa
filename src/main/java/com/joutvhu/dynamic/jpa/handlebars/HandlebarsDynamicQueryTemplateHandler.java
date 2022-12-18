package com.joutvhu.dynamic.jpa.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateLoader;
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
public class HandlebarsDynamicQueryTemplateHandler implements
        DynamicQueryTemplateHandler<Template>, ResourceLoaderAware, InitializingBean {

    private static final Log log = LogFactory.getLog(HandlebarsDynamicQueryTemplateHandler.class);

    private static final Map<String, DynamicQueryTemplate<Template>> templateCache = new ConcurrentHashMap<>();
    private static final HandlebarsTemplateConfiguration config
            = HandlebarsTemplateConfiguration.instanceWithDefault();

    private static final Handlebars handlebars = config.handlebars();

    private String encoding = "UTF-8";
    private String templateLocation = "classpath:/query";
    private String suffix = ".dsql";
    private ResourceLoader resourceLoader;

    @Override
    public DynamicQueryTemplate<Template> createTemplateWithString(String name, String content) {
        try {
            return loadTemplateContent(name, content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DynamicQueryTemplate<Template> findTemplateFile(String name) {
        return templateCache.get(name);
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
    public void afterPropertiesSet()  {
        configureTemplateLoader();

        String templateLocationPattern = getTemplateLocationPattern();
        PathMatchingResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(templateLocationPattern);

        for (Resource resource : resources) {
            DynamicTemplateResolver.of(resource).encoding(encoding).load((templateName, content) -> {
                loadTemplateContent(templateName, content);
            });
        }
    }

    private String getTemplateLocationPattern() {
        if (StringUtils.isNotBlank(templateLocation)) {
            return templateLocation.contains(suffix) ? templateLocation : templateLocation + "/**/*" + suffix;
        } else {
            return "classpath:/**/*" + suffix;
        }
    }

    private TemplateLoader configureTemplateLoader() {
        TemplateLoader sqlTemplateLoader = config.templateLoader();
        sqlTemplateLoader.setPrefix(this.templateLocation);
        sqlTemplateLoader.setSuffix(this.suffix);
        sqlTemplateLoader.setCharset(Charset.forName(this.encoding));
        return sqlTemplateLoader;
    }

    private HandlebarsDynamicQueryTemplate loadTemplateContent(String templateName, String content) throws IOException {
        Template template = handlebars.compileInline(content);
        HandlebarsDynamicQueryTemplate dynamicTemplate
                = new HandlebarsDynamicQueryTemplate(template);
        templateCache.put(templateName, dynamicTemplate);
        return dynamicTemplate;
    }
}
