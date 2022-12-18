package com.joutvhu.dynamic.commons.freemarker.directive;

import freemarker.core.Environment;
import freemarker.ext.beans.InvalidPropertyException;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The trim directive knows to only insert the prefix and suffix if there is any content returned by the containing tags
 * And the trim directive will remove prefixOverrides and suffixOverrides in the content
 * They are used in templates like {@code <@trim prefix="where (" prefixOverrides=["and ", "or "] suffix=")" suffixOverrides=[" and", " or"]>...</@trim>}
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public class TrimDirective implements TemplateDirectiveModel {
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        TrimSymbol symbols = new TrimSymbol(params);
        if (body != null)
            TrimWriter.of(env.getOut(), symbols).render(body);
    }

    /**
     * Trim directive writer
     */
    public static class TrimWriter extends Writer {
        private final Writer out;
        private final TrimSymbol symbols;
        private final StringBuilder contentBuilder = new StringBuilder();

        public static TrimWriter of(Writer out, TrimSymbol symbols) {
            return new TrimWriter(out, symbols);
        }

        public TrimWriter(Writer out, TrimSymbol symbols) {
            this.out = out;
            this.symbols = symbols;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            String content = String.copyValueOf(cbuf);
            this.contentBuilder.append(content);
        }

        public void afterWrite() throws IOException {
            String content = this.contentBuilder.toString();

            for (String prefix : symbols.prefixOverrides)
                content = Pattern.compile("^[ \\t\\n]*" + escapeRegular(prefix), Pattern.CASE_INSENSITIVE)
                        .matcher(content)
                        .replaceAll("");
            for (String suffix : symbols.suffixOverrides)
                content = Pattern.compile(escapeRegular(suffix) + "[ \\t\\n]*$", Pattern.CASE_INSENSITIVE)
                        .matcher(content)
                        .replaceAll("");

            content = content.trim();
            if (!content.isEmpty()) {
                if (symbols.prefix != null)
                    content = symbols.prefix + " " + content;
                if (symbols.suffix != null)
                    content = content + " " + symbols.suffix;
            }
            content = " " + content + " ";

            out.write(content);
        }

        public void render(TemplateDirectiveBody body) throws IOException, TemplateException {
            body.render(this);
            this.afterWrite();
        }

        private String escapeRegular(String regex) {
            return Pattern.compile("([-/\\\\^$*+?.()|\\[\\]{}])").matcher(regex).replaceAll("\\\\$1");
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    /**
     * Trim directive param container
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class TrimSymbol {
        private String prefix;
        private List<String> prefixOverrides;
        private String suffix;
        private List<String> suffixOverrides;

        public TrimSymbol(String prefix, String suffix, String... overrides) {
            this.prefix = prefix;
            this.suffix = suffix;

            List<String> listOverrides = new ArrayList<>();
            Collections.addAll(listOverrides, overrides);
            this.prefixOverrides = listOverrides;
            this.suffixOverrides = listOverrides;
        }

        /**
         * Get params of trim directive from params map
         *
         * @param params map
         * @throws TemplateException if invalid params
         */
        public TrimSymbol(Map<String, Object> params) throws TemplateException {
            this.prefix = getStringParam(params, "prefix");
            this.prefixOverrides = getListParam(params, "prefixOverrides");
            this.suffix = getStringParam(params, "suffix");
            this.suffixOverrides = getListParam(params, "suffixOverrides");

            if (prefix == null && prefixOverrides.isEmpty() && suffix == null && suffixOverrides.isEmpty())
                throw new TemplateModelException("The trim directive requires at least one of the following parameters: " +
                        "prefix, prefixOverrides, suffix, suffixOverrides");
        }

        private String getStringParam(Map<String, Object> params, String name) throws TemplateException {
            if (params.containsKey(name)) {
                Object param = params.get(name);
                if (param instanceof SimpleScalar)
                    return getStringFromSimpleScalar((SimpleScalar) param, name);
            }
            return null;
        }

        private String getStringFromSimpleScalar(SimpleScalar simpleScalar, String name) throws TemplateException {
            String value = simpleScalar.getAsString();
            if (value != null && !value.trim().isEmpty())
                return value;
            else throw new InvalidPropertyException("The " + name + " param cannot be empty string or spaces.");
        }

        private List<String> getListParam(Map<String, Object> params, String name) throws TemplateException {
            List<String> result = new ArrayList<>();
            if (params.containsKey(name)) {
                Object param = params.get(name);
                if (param instanceof SimpleScalar)
                    result.add(getStringFromSimpleScalar((SimpleScalar) param, name));
                else if (param instanceof SimpleSequence)
                    return getListFromSimpleSequence((SimpleSequence) param, name);
            }
            return result;
        }

        private List<String> getListFromSimpleSequence(SimpleSequence simpleSequence, String name) throws TemplateException {
            List<String> result = new ArrayList<>();
            for (int i = 0, len = simpleSequence.size(); i < len; i++) {
                String value = simpleSequence.get(i).toString();
                if (value != null && !value.trim().isEmpty())
                    result.add(value);
                else throw new InvalidPropertyException("The " + name + " param cannot be contains empty or spaces.");
            }
            return result;
        }
    }
}
