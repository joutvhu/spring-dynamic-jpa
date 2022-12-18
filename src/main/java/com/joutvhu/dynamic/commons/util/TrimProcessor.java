
package com.joutvhu.dynamic.commons.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.regex.Pattern;

@Getter
@Setter
@AllArgsConstructor
public class TrimProcessor {

    public String process(TrimSymbol symbols, String content) throws IOException {
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

    private String escapeRegular(String regex) {
        return Pattern.compile("([-/\\\\^$*+?.()|\\[\\]{}])").matcher(regex).replaceAll("\\\\$1");
    }

}