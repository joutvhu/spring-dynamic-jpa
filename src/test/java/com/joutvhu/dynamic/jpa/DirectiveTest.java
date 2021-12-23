package com.joutvhu.dynamic.jpa;

import com.joutvhu.dynamic.jpa.util.TemplateConfiguration;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;

public class DirectiveTest {
    @Test
    public void where1Test() throws IOException, TemplateException {
        Configuration cfg = TemplateConfiguration.instanceWithDefault().configuration();
        Template template = new Template("where", "<@where> and (abcd) or</@where>", cfg);
        String queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, new HashMap<>());
        Assertions.assertEquals(" where (abcd) ", queryString);
    }

    @Test
    public void where2Test() throws IOException, TemplateException {
        Configuration cfg = TemplateConfiguration.instanceWithDefault().configuration();
        Template template = new Template("where", "<@where> \nor\n   abcd  \nand\n </@where>", cfg);
        String queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, new HashMap<>());
        Assertions.assertEquals(" where abcd ", queryString);
    }

    @Test
    public void where3Test() throws IOException, TemplateException {
        Configuration cfg = TemplateConfiguration.instanceWithDefault().configuration();
        Template template = new Template("where", "<@where> \nOR\n   abcd  \nAND\n </@where>", cfg);
        String queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, new HashMap<>());
        Assertions.assertEquals(" where abcd ", queryString);
    }

    @Test
    public void set1Test() throws IOException, TemplateException {
        Configuration cfg = TemplateConfiguration.instanceWithDefault().configuration();
        Template template = new Template("set", "<@set> ,abcd, </@set>", cfg);
        String queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, new HashMap<>());
        Assertions.assertEquals(" set abcd ", queryString);
    }

    @Test
    public void set2Test() throws IOException, TemplateException {
        Configuration cfg = TemplateConfiguration.instanceWithDefault().configuration();
        Template template = new Template("set", "<@set>\n , abcd ,</@set>", cfg);
        String queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, new HashMap<>());
        Assertions.assertEquals(" set abcd ", queryString);
    }

    @Test
    public void trim1Test() throws IOException, TemplateException {
        Configuration cfg = TemplateConfiguration.instanceWithDefault().configuration();
        Template template = new Template("trim", "<@trim prefix=\"69\" prefixOverrides=[\"a\"] suffix=\"e\" suffixOverrides=[\"b\"]> a abcd b</@trim>", cfg);
        String queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, new HashMap<>());
        Assertions.assertEquals(" 69 abcd e ", queryString);
    }
}
