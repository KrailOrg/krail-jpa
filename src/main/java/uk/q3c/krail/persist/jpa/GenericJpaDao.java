package uk.q3c.krail.persist.jpa;

import org.apache.onami.persist.EntityManagerProvider;

import java.util.List;

/**
 * Created by David Sowerby on 01/01/15.
 */
public interface GenericJpaDao {
    EntityManagerProvider getEntityManagerProvider();

    void setEntityManagerProvider(EntityManagerProvider entityManagerProvider);

    <E extends JpaEntity> void persist(E entity);

    <E extends JpaEntity> void merge(E entity);

    <E extends JpaEntity> List<E> findAll(Class<E> entityClass);

    <E extends JpaEntity> void delete(E entity);

    <E extends JpaEntity> E findFromId(Class<E> entityClass, Object id);
}
