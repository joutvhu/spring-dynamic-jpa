package com.joutvhu.dynamic.jpa.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;

import static org.apache.commons.lang3.Validate.notNull;

@NoArgsConstructor
public enum CustomStringHelpers implements Helper<Object> {

    startsWith {
        @Override
        @SneakyThrows
        protected CharSequence safeApply(final Object value, final Options options) {
            boolean result = value.toString().startsWith(options.param(0));
            return result ? options.fn() : options.inverse();
        }
    },

    endsWith {
        @Override
        @SneakyThrows
        protected CharSequence safeApply(final Object value, final Options options) {
            boolean result = value.toString().endsWith(options.param(0));
            return result ? options.fn() : options.inverse();
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
        CustomStringHelpers[] helpers = values();
        for (CustomStringHelpers helper : helpers) {
            helper.registerHelper(handlebars);
        }
    }
}
