package com.joutvhu.dynamic.commons.freemarker;

import com.joutvhu.dynamic.commons.freemarker.directive.SetDirective;
import com.joutvhu.dynamic.commons.freemarker.directive.TrimDirective;
import com.joutvhu.dynamic.commons.freemarker.directive.WhereDirective;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

/**
 * Freemarker configuration builder.
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public class FreemarkerTemplateConfiguration {
    private Configuration cfg;

    protected FreemarkerTemplateConfiguration() {
        this.cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public static FreemarkerTemplateConfiguration instance() {
        return new FreemarkerTemplateConfiguration();
    }

    public static FreemarkerTemplateConfiguration instanceWithDefault() {
        return instance().applyDefault();
    }

    public FreemarkerTemplateConfiguration applyDefault() {
        cfg.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
        cfg.setInterpolationSyntax(Configuration.DOLLAR_INTERPOLATION_SYNTAX);

        cfg.setSharedVariable("trim", new TrimDirective());
        cfg.setSharedVariable("set", new SetDirective());
        cfg.setSharedVariable("where", new WhereDirective());

        return this;
    }

    public FreemarkerTemplateConfiguration templateLoader(TemplateLoader templateLoader) {
        cfg.setTemplateLoader(templateLoader);
        return this;
    }

    public Configuration configuration() {
        return cfg;
    }
}
