package com.joutvhu.dynamic.jpa.freemarker;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import freemarker.cache.TemplateLoader;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class ConcurrentStringTemplateLoader implements TemplateLoader {
    private final Map<String, StringTemplateSource> templates;

    public ConcurrentStringTemplateLoader() {
        this.templates = new ConcurrentHashMap<>();
    }

    public boolean hasTemplateSource(String name) {
        return templates.containsKey(name);
    }

    public void putTemplate(String name, String templateSource) {
        putTemplate(name, templateSource, System.nanoTime());
    }

    public void putTemplate(String name, String templateSource, long lastModified) {
        templates.put(name, new StringTemplateSource(name, templateSource, lastModified));
    }

    public void removeTemplateSource(String name) {
        templates.remove(name);
    }

    public void closeTemplateSource(Object templateSource) {}

    public Object findTemplateSource(String name) {
        StringTemplateSource stringTemplateSource = templates.get(name);
        templates.remove(name);
        return stringTemplateSource;
    }

    public long getLastModified(Object templateSource) {
        return ((StringTemplateSource) templateSource).lastModified;
    }

    public Reader getReader(Object templateSource, String encoding) {
        return new StringReader(((StringTemplateSource) templateSource).source);
    }

    @Getter
    @EqualsAndHashCode(of = "name")
    public static class StringTemplateSource {
        private final String name;
        private final String source;
        private final long lastModified;

        public StringTemplateSource(String name, String source, long lastModified) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }
            if (source == null) {
                throw new IllegalArgumentException("source == null");
            }
            if (lastModified < -1L) {
                throw new IllegalArgumentException("lastModified < -1L");
            }
            this.name = name;
            this.source = source;
            this.lastModified = lastModified;
        }
    }
}
