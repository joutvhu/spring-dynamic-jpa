package com.joutvhu.dynamic.jpa.util;

/**
 * Callback when found a query template {@link freemarker.template.Template}
 *
 * @author Giao Ho
 * @see 1.0.0
 */
public interface NamedTemplateCallback {
    void process(String templateName, String content);
}
