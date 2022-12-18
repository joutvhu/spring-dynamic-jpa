package com.joutvhu.dynamic.commons.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Read and parse template query files into query templates
 *
 * @author Giao Ho
 * @since 1.0.0
 */
public class DynamicTemplateResolver {
    private final Resource resource;
    private List<String> lines;
    private String encoding = "UTF-8";

    public DynamicTemplateResolver(Resource resource) {
        this.resource = resource;
    }

    public static DynamicTemplateResolver of(Resource resource) {
        return new DynamicTemplateResolver(resource);
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        BufferedReader reader = toBufferedReader(new InputStreamReader(input,
                encoding == null ? Charset.defaultCharset() : Charset.forName(encoding)));
        List<String> list = new ArrayList<>();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            list.add(line);
        }

        return list;
    }

    public DynamicTemplateResolver encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    private boolean isNameLine(String line) {
        return StringUtils.startsWith(line, "--");
    }

    private boolean isNameLine(int index) {
        return isNameLine(lines.get(index));
    }

    public void load(NamedTemplateCallback callback) throws Exception {
        InputStream inputStream = resource.getInputStream();
        lines = DynamicTemplateResolver.readLines(inputStream, encoding);

        int index = 0;
        String name = null;
        int total = lines.size();
        StringBuilder content = new StringBuilder();

        while (index < total) {
            do {
                String line = lines.get(index);
                if (isNameLine(line))
                    name = StringUtils.trim(StringUtils.substring(line, 2));
                else {
                    line = StringUtils.trimToNull(line);
                    if (line != null)
                        content.append(line).append(" ");
                }
                index++;
            } while (index < total && !isNameLine(index));

            // Next template
            if (name != null)
                callback.process(name, content.toString());
            name = null;
            content = new StringBuilder();
        }
    }
}
