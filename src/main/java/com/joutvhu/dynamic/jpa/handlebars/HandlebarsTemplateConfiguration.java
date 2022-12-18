package com.joutvhu.dynamic.jpa.handlebars;


import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.joutvhu.dynamic.jpa.handlebars.helpers.CustomDirectiveHelpers;
import com.joutvhu.dynamic.jpa.handlebars.helpers.CustomStringHelpers;

/**
 * HandlebarsTemplateConfiguration
 *
 * @author Jean Sossmeier
 * @since 2.0.0
 */
public class HandlebarsTemplateConfiguration {
    private TemplateLoader templateLoader;
    private Handlebars handlebars;

    protected HandlebarsTemplateConfiguration() {
        this.templateLoader = new ClassPathTemplateLoader();
        this.handlebars = new Handlebars(templateLoader);
    }
    protected HandlebarsTemplateConfiguration(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
        this.handlebars = new Handlebars(templateLoader);
    }

    public static HandlebarsTemplateConfiguration instance() {
        return new HandlebarsTemplateConfiguration();
    }

    public static HandlebarsTemplateConfiguration instanceWithDefault() {
        return instance().applyDefault();
    }

    public HandlebarsTemplateConfiguration applyDefault() {
        CustomStringHelpers.register(handlebars);
        CustomDirectiveHelpers.register(handlebars);
        return this;
    }

    public Handlebars handlebars() {
        return handlebars;
    }
    public TemplateLoader templateLoader() {
        return templateLoader;
    }
}
