package uk.q3c.krail.persist.jpa;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.CachingBatchableLocalEntityProvider;

import javax.persistence.EntityManagerFactory;
import java.lang.annotation.Annotation;

/**
 * Created by David Sowerby on 03/01/15.
 */
public class DefaultJpaContainerProvider implements JpaContainerProvider {

    private Injector injector;

    @Inject
    protected DefaultJpaContainerProvider(Injector injector) {
        this.injector = injector;
    }


    @Override
    public <E extends JpaEntity> JPAContainer<E> get(Class<? extends Annotation> annotatedBy, Class<E> entityClass,
                                                     ContainerType containerType) {

        com.vaadin.addon.jpacontainer.EntityProvider<E> containerProvider = null;


        switch (containerType) {
            case CACHED:
                containerProvider = new CachingBatchableLocalEntityProvider<E>(entityClass);
                break;
        }
        Key<EntityManagerFactory> key = Key.get(EntityManagerFactory.class, annotatedBy);
        EntityManagerFactory emf = injector.getInstance(key);
        containerProvider.setEntityManager(emf.createEntityManager());
        JPAContainer container = new JPAContainer(entityClass);
        container.setEntityProvider(containerProvider);
        return container;
    }
}
