package uk.q3c.krail.persist.jpa;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import java.lang.annotation.Annotation;

/**
 * A Krail replacement for {@link JPAContainerFactory}
 * <p>
 * Created by David Sowerby on 03/01/15.
 */
public interface JpaContainerProvider {
    public enum ContainerType {CACHED}

    <E extends JpaEntity> JPAContainer<E> get(Class<? extends Annotation> annotatedBy, Class<E> entityClass,
                                              DefaultJpaContainerProvider.ContainerType containerType);
}
