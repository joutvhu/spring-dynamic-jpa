package com.joutvhu.dynamic.jpa.support;

import com.joutvhu.dynamic.commons.util.ApplicationContextHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * Special adapter for Springs {@link DynamicJpaRepositoryFactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 *
 * @author Giao Ho
 * @since 2.x.1
 */
public class DynamicJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID>
        implements ApplicationContextAware {
    private EntityPathResolver entityPathResolver;

    /**
     * Creates a new {@link DynamicJpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public DynamicJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        DynamicJpaRepositoryFactory jpaRepositoryFactory = new DynamicJpaRepositoryFactory(entityManager);
        jpaRepositoryFactory.setEntityPathResolver(entityPathResolver);

        return jpaRepositoryFactory;
    }

    @Autowired
    @Override
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.appContext = applicationContext;
    }
}
