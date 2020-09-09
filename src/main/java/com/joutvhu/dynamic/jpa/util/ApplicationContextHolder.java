package com.joutvhu.dynamic.jpa.util;

import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

/**
 * Help call method of {@link ApplicationContext} from any class.
 *
 * @author Giao Ho
 * @see 1.0.0
 */
@UtilityClass
public class ApplicationContextHolder {
    public ApplicationContext appContext;

    public <T> Collection<T> getBeansOfType(Class<T> clazz) {
        return appContext.getBeansOfType(clazz).values();
    }

    public <T> T getBean(Class<T> clazz) {
        return appContext.getBean(clazz);
    }
}