package com.joutvhu.dynamic.commons.freemarker.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The where directive knows to only insert "WHERE" if there is any content returned by the containing tags,
 * If that content begins or ends with "AND" or "OR", it knows to strip it off.
 * They are used in templates like {@code <@where>...</@where>}
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public class WhereDirective implements TemplateDirectiveModel {
    private static final TrimDirective.TrimSymbol symbols = new TrimDirective.TrimSymbol(
            "where", getOverrides(true, "and", "or"),
            null, getOverrides(false, "and", "or"));

    private static final List<String> getOverrides(boolean prefix, String... overrides) {
        List<String> result = new ArrayList<>();
        for (String o : overrides) {
            result.add(prefix ? o + " " : " " + o);
            result.add(prefix ? o + "\n" : "\n" + o);
            result.add(prefix ? o + "\t" : "\t" + o);
        }
        return result;
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        if (!params.isEmpty())
            throw new TemplateModelException("This directive doesn't allow parameters.");

        if (body != null)
            TrimDirective.TrimWriter.of(env.getOut(), symbols).render(body);
    }
}
