
package com.joutvhu.dynamic.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;

class DirectiveTest {
    @ParameterizedTest
    @CsvSource({
            "'where', '<@where> and (abcd) or</@where>', ' where (abcd) '",
            "'where', '<@where> \nor\n   abcd  \nand\n </@where>', ' where abcd '",
            "'where', '<@where> \nOR\n   abcd  \nAND\n </@where>', ' where abcd '",
            "'set', '<@set> ,abcd, </@set>', ' set abcd '",
            "'set', '<@set>\n , abcd ,</@set>', ' set abcd '",
            "'trim', '<@trim prefix=\"69\" prefixOverrides=[\"a\"] suffix=\"e\" suffixOverrides=[\"b\"]> a abcd b</@trim>', ' 69 abcd e '",
    })
    void testDirectives(String name, String source, String expected) throws IOException, TemplateException {
        Configuration cfg = FreemarkerTemplateConfiguration.instanceWithDefault().configuration();
        Template template = new Template(name, source, cfg);
        String queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, new HashMap<>());
        Assertions.assertEquals(expected, queryString);
    }
}
