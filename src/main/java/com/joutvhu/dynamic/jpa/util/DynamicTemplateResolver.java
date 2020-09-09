package com.joutvhu.dynamic.jpa.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicTemplateResolver {
    private String encoding = "UTF-8";

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        BufferedReader reader = toBufferedReader(new InputStreamReader(input,
                encoding == null ? Charset.defaultCharset() : Charset.forName(encoding)));
        List<String> list = new ArrayList();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            list.add(line);
        }

        return list;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Iterator<Void> doInTemplateResource(Resource resource, final NamedTemplateCallback callback) throws Exception {
        InputStream inputStream = resource.getInputStream();
        final List<String> lines = readLines(inputStream, encoding);
        return new Iterator<Void>() {
            String name;
            StringBuilder content = new StringBuilder();
            int index = 0;
            int total = lines.size();

            @Override
            public boolean hasNext() {
                return index < total;
            }

            @Override
            public Void next() {
                do {
                    String line = lines.get(index);
                    if (isNameLine(line)) {
                        name = StringUtils.trim(StringUtils.remove(line, "--"));
                    } else {
                        line = StringUtils.trimToNull(line);
                        if (line != null) {
                            content.append(line).append(" ");
                        }
                    }
                    index++;
                } while (!isLastLine() && !isNextNameLine());

                //next template
                callback.process(name, content.toString());
                name = null;
                content = new StringBuilder();
                return null;
            }

            @Override
            public void remove() {
                //ignore
            }

            private boolean isNameLine(String line) {
                return StringUtils.contains(line, "--");
            }

            private boolean isNextNameLine() {
                String line = lines.get(index);
                return isNameLine(line);
            }

            private boolean isLastLine() {
                return index == total;
            }
        };
    }
}
