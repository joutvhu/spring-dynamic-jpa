package com.joutvhu.dynamic.commons.util;

/**
 * Callback when found a query template {@link freemarker.template.Template}
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public interface NamedTemplateCallback {
    void process(String templateName, String content);
}
