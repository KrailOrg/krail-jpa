package uk.q3c.krail.persist.jpa;

import org.apache.onami.persist.PersistenceModule;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by David Sowerby on 30/12/14.
 */
public abstract class JpaUnitModule extends PersistenceModule {

    /**
     * Adds a JPA persistence unit and associates it with an annotation - the annotation is required in order to
     * identify different EntityManagers if you are using multiple persistence units.  If you are using only one
     * persistence unit, see {@link #addPersistenceUnit(String, Map)}
     *
     * @param puName
     *         the persistence unit name - must be defined in persistence.xml
     * @param annotation
     *         - the annotation class to associate the persistence unit to.  Must be a @BindingAnnotation
     * @param map
     *         (may be null) a map of properties which can be used to supplement or override the values in
     *         persistence.xml.  {@link JpaConfig} is a useful helper class to define the map
     */
    protected void addPersistenceUnit(String puName, Class<? extends Annotation> annotation, Map<String, Object> map) {
        EntityManagerFactory entityManagerFactory;
        entityManagerFactory = Persistence.createEntityManagerFactory(puName, map);
        //bind factory for use with JPAContainer setup
        bind(EntityManagerFactory.class).annotatedWith(annotation)
                                        .toInstance(entityManagerFactory);
        bindContainerManagedPersistenceUnit(entityManagerFactory).annotatedWith(annotation);
    }

    /**
     * Adds a single JPA persistence unit.  If you require multiple persistence units, an annotation is required in
     * order to identify different EntityManagers - use {@link #addPersistenceUnit(String, Class, Map)} instead.  You
     * may want to consider using an annotation even if you currently only need a single persistence unit - it would
     * be much easier then to add another later, while maintaining clarity of which persistence unit is used where.
     *
     * @param puName
     *         the persistence unit name - must be defined in persistence.xml
     * @param map
     *         (may be null) a map of properties which can be used to supplement or override the values in
     *         persistence.xml.  {@link JpaConfig} is a useful helper class to define the map
     */
    protected void addPersistenceUnit(String puName, Map<String, Object> map) {
        EntityManagerFactory entityManagerFactory;
        entityManagerFactory = Persistence.createEntityManagerFactory(puName, map);
        bind(EntityManagerFactory.class).toInstance(entityManagerFactory);
        bindContainerManagedPersistenceUnit(entityManagerFactory);
    }

}
