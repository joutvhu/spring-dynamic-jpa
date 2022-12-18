package com.joutvhu.dynamic.commons.freemarker;

import com.joutvhu.dynamic.jpa.DynamicQueryTemplate;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FreemarkerDynamicQueryTemplate implements DynamicQueryTemplate<Template> {
    private final Template template;

    @Override
    public Template getTemplate() {
        return this.template;
    }
}
