package uk.q3c.krail.persist.jpa;

import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.List;

/**
 * A collection of common persistence actions for JPA data sources.  Note that this class must be injected for
 *
 * @Transactional to take effect (it is using Guice AOP) and you must call {@link #setEntityManagerProvider
 * (EntityManagerProvider)} with your chosen EntityManagerProvider before calling any other methods.
 * <p>
 * This is necessary to allow Krail to support multiple JPA persistence units but still provide a generic JPA DAO
 * <p>
 * <p>
 * Created by David Sowerby on 30/12/14.
 */
public class DefaultGenericJpaDao implements GenericJpaDao {
    private static Logger log = LoggerFactory.getLogger(DefaultGenericJpaDao.class);

    private EntityManagerProvider entityManagerProvider;

    public DefaultGenericJpaDao() {
    }

    @Override
    public EntityManagerProvider getEntityManagerProvider() {
        return entityManagerProvider;
    }

    @Override
    public void setEntityManagerProvider(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    /**
     * Inserts a new record into the database and attaches the entity to the entityManager.
     *
     * @param entity
     */
    @Override
    @Transactional
    public <E extends JpaEntity> void persist(E entity) {
        EntityManager entityManager = entityManagerProvider.get();
        entityManager.persist(entity);
        log.debug("{} persisted", entity);
    }

    /**
     * Find an attached entity with the same id and update it. If one exists update and return the already attached
     * entity. If one doesn't exist, inserts a new record into the database & attaches the entity to the entityManager.
     *
     * @param entity
     */
    @Override
    @Transactional
    public <E extends JpaEntity> void merge(E entity) {
        EntityManager entityManager = entityManagerProvider.get();
        entityManager.merge(entity);
        log.debug("{} merged", entity);
    }

    @Override
    @Transactional
    public <E extends JpaEntity> List<E> findAll(Class<E> entityClass) {
        EntityManager entityManager = entityManagerProvider.get();
        Query q = entityManager.createQuery("select t from " + tableName(entityClass) + " t");
        log.debug("processing query {}", q);
        List<E> results = q.getResultList();
        log.debug("Returning all {} instances of {}", results.size(), entityClass);
        return results;
    }

    protected <E extends JpaEntity> String tableName(Class<E> entityClass) {
        Metamodel meta = entityManagerProvider.get()
                                              .getMetamodel();
        EntityType<E> entityType = meta.entity(entityClass);

        //Check whether @Table annotation is present on the class.
        Table t = entityClass.getAnnotation(Table.class);

        //If no Table annotation use the default (simple class name)
        String tableName = (t == null) ? entityType.getName() : t.name();
        return tableName;
    }

    @Override
    public <E extends JpaEntity> void delete(E entity) {

    }

    @Override
    public <E extends JpaEntity> E findFromId(Class<E> entityClass, Object id) {
        return null;
    }
}
