package com.joutvhu.dynamic.commons.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.joutvhu.dynamic.commons.freemarker.directive.TrimDirective;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.notNull;

@NoArgsConstructor
public enum CustomDirectiveHelpers implements Helper<Object> {

    where {
        @Override
        @SneakyThrows
        protected CharSequence safeApply(final Object value, final Options options) {
            CharSequence content = options.fn();
            return normalize(content.toString());
        }

        private final TrimDirective.TrimSymbol symbols = new TrimDirective.TrimSymbol(
                "where", getOverrides(true, "and", "or"),
                null, getOverrides(false, "and", "or"));

        private final List<String> getOverrides(boolean prefix, String... overrides) {
            List<String> result = new ArrayList<>();
            for (String o : overrides) {
                result.add(prefix ? o + " " : " " + o);
                result.add(prefix ? o + "\n" : "\n" + o);
                result.add(prefix ? o + "\t" : "\t" + o);
            }
            return result;
        }

        private String escapeRegular(String regex) {
            return Pattern.compile("([-/\\\\^$*+?.()|\\[\\]{}])").matcher(regex).replaceAll("\\\\$1");
        }

        public String normalize(String content) throws IOException {
            for (String prefix : symbols.getPrefixOverrides())
                content = Pattern.compile("^[ \\t\\n]*" + escapeRegular(prefix), Pattern.CASE_INSENSITIVE)
                        .matcher(content)
                        .replaceAll("");

            for (String suffix : symbols.getSuffixOverrides())
                content = Pattern.compile(escapeRegular(suffix) + "[ \\t\\n]*$", Pattern.CASE_INSENSITIVE)
                        .matcher(content)
                        .replaceAll("");

            content = content.trim();
            if (!content.isEmpty()) {
                if (symbols.getPrefix() != null)
                    content = symbols.getPrefix() + " " + content;
                if (symbols.getSuffix() != null)
                    content = content + " " + symbols.getSuffix();
            }
            content = " " + content + " ";
            return content;
        }
    };

    @Override
    public Object apply(final Object context, final Options options) throws IOException {
        if (options.isFalsy(context)) {
            Object param = options.param(0, null);
            return param == null ? null : param.toString();
        }
        return safeApply(context, options);
    }

    /**
     * Apply the helper to the context.
     *
     * @param context The context object (param=0).
     * @param options The options object.
     * @return A string result.
     */
    protected abstract CharSequence safeApply(Object context, Options options);

    /**
     * Register the helper in a handlebars instance.
     *
     * @param handlebars A handlebars object. Required.
     */
    public void registerHelper(final Handlebars handlebars) {
        notNull(handlebars, "The handlebars is required.");
        handlebars.registerHelper(name(), this);
    }

    /**
     * Register all the text helpers.
     *
     * @param handlebars The helper's owner. Required.
     */
    public static void register(final Handlebars handlebars) {
        notNull(handlebars, "A handlebars object is required.");
        CustomDirectiveHelpers[] helpers = values();
        for (CustomDirectiveHelpers helper : helpers) {
            helper.registerHelper(handlebars);
        }
    }
}
