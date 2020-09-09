package com.joutvhu.dynamic.jpa.support;

import org.springframework.context.ApplicationContext;

import java.util.Collection;

/**
 * Context holder
 *
 * @author Giao Ho
 * @see 1.0.0
 */
public class ContextHolder {
    public static ApplicationContext appContext;

    public static <T> Collection<T> getBeansOfType(Class<T> clazz) {
        return appContext.getBeansOfType(clazz).values();
    }

    public static <T> T getBean(Class<T> clazz) {
        return appContext.getBean(clazz);
    }
}