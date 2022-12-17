package com.joutvhu.dynamic.commons.handlebars;

import com.github.jknack.handlebars.Template;
import com.joutvhu.dynamic.jpa.DynamicQueryTemplate;
import lombok.RequiredArgsConstructor;

/**
 * HandlebarsDynamicQueryTemplate
 *
 * @author Jean Sossmeier
 * @since 2.0.0
 */
@RequiredArgsConstructor
public class HandlebarsDynamicQueryTemplate implements DynamicQueryTemplate<Template> {
    private final Template template;

    @Override
    public Template getTemplate() {
        return this.template;
    }
}
