package com.joutvhu.dynamic.jpa.util;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

/**
 * Freemarker configuration builder.
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public class TemplateConfiguration {
    private Configuration cfg;

    protected TemplateConfiguration() {
        this.cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public static TemplateConfiguration instance() {
        return new TemplateConfiguration();
    }

    public static TemplateConfiguration instanceWithDefault() {
        return instance().applyDefault();
    }

    public TemplateConfiguration applyDefault() {
        cfg.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
        cfg.setInterpolationSyntax(Configuration.DOLLAR_INTERPOLATION_SYNTAX);
        return this;
    }

    public TemplateConfiguration templateLoader(TemplateLoader templateLoader) {
        cfg.setTemplateLoader(templateLoader);
        return this;
    }

    public Configuration configuration() {
        return cfg;
    }
}
