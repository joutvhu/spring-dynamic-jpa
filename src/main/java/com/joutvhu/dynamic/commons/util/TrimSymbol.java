package com.joutvhu.dynamic.commons.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Trim symbols param container
 */
@Getter
@Setter
@AllArgsConstructor
public class TrimSymbol {
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

    public static List<String> getOverrides(boolean prefix, String... overrides) {
        List<String> result = new ArrayList<>();
        for (String o : overrides) {
            result.add(prefix ? o + " " : " " + o);
            result.add(prefix ? o + "\n" : "\n" + o);
            result.add(prefix ? o + "\t" : "\t" + o);
        }
        return result;
    }
}