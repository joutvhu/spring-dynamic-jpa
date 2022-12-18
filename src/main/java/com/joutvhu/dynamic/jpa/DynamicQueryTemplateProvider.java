package com.joutvhu.dynamic.jpa;

public interface DynamicQueryTemplateProvider<T> {
    DynamicQueryTemplate<T> createTemplateWithString(String name, String query);
    DynamicQueryTemplate<T> findTemplateFile(String name);
}
