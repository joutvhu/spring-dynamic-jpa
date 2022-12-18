package com.joutvhu.dynamic.commons.freemarker;

import com.joutvhu.dynamic.commons.util.DynamicTemplateResolver;
import com.joutvhu.dynamic.jpa.DynamicQueryTemplate;
import com.joutvhu.dynamic.jpa.DynamicQueryTemplateHandler;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
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
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

/**
 * DynamicQueryTemplates
 *
 * @author Giao Ho
 * @since 1.0.0
 */
@NoArgsConstructor
public class FreemarkerDynamicQueryTemplateHandler implements
        DynamicQueryTemplateHandler<Template>,
        ResourceLoaderAware,
        InitializingBean {

    private static final Log log = LogFactory.getLog(FreemarkerDynamicQueryTemplateHandler.class);

    private static StringTemplateLoader sqlTemplateLoader = new StringTemplateLoader();
    private static Configuration cfg = FreemarkerTemplateConfiguration.instanceWithDefault()
            .templateLoader(sqlTemplateLoader)
            .configuration();

    private String encoding = "UTF-8";
    private String templateLocation = "classpath:/query";
    private String suffix = ".dsql";
    private ResourceLoader resourceLoader;

    @Override
    public DynamicQueryTemplate<Template> createTemplate(String name, String content) {
        try {
            return new FreemarkerDynamicQueryTemplate(new Template(name, content, cfg));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DynamicQueryTemplate<Template> findTemplate(String name) {
        try {
            Template template = cfg.getTemplate(name, encoding);
            return new FreemarkerDynamicQueryTemplate(template);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    @SneakyThrows
    public String processTemplate(DynamicQueryTemplate<Template> template, Map<String, Object> params) {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template.getTemplate(), params);
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
    public void afterPropertiesSet() throws Exception {
        String pattern;
        if (StringUtils.isNotBlank(templateLocation))
            pattern = templateLocation.contains(suffix) ? templateLocation : templateLocation + "/**/*" + suffix;
        else pattern = "classpath:/**/*" + suffix;

        PathMatchingResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(pattern);

        for (Resource resource : resources) {
            DynamicTemplateResolver.of(resource).encoding(encoding).load((templateName, content) -> {
                Object src = sqlTemplateLoader.findTemplateSource(templateName);
                if (src != null)
                    log.warn("Found duplicate template key, will replace the value, key: " + templateName);
                sqlTemplateLoader.putTemplate(templateName, content);
            });
        }
    }
}
