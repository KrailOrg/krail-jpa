/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.persist.jpa;

import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.Transactional;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.data.KrailEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A general purpose JPA DAO Base implementation with generic methods.  Sub-class for specific ID and VER types.  Transaction control must be provided by the
 * caller, and each method needs an EntityManager instance as a parameter.  The same can be achieved using an {@link EntityManager} directly - this class,
 * however, may be useful as a base where the developer prefers to keep queries in one place.
 * <p>
 * <p>
 * Created by David Sowerby on 08/04/15.
 */
public abstract class BaseJpaDao<ID, VER> implements JpaDao<ID, VER> {

    private static Logger log = LoggerFactory.getLogger(BaseJpaDao.class);
    private EntityManagerProvider entityManagerProvider;


    public BaseJpaDao(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Nonnull
    public <E extends KrailEntity<ID, VER>> E save(@Nonnull E entity) {
        checkNotNull(entity);
        EntityManager entityManager = entityManagerProvider.get();
        

        if (entity.getId() == null) {
            entityManager.persist(entity);
            log.debug("{} persisted", entity);
            return entity;
        }


        @SuppressWarnings("unchecked") Optional<E> fEntity = findById((Class<E>) entity.getClass(), entity.getId());
        if (fEntity.isPresent()) {
            E managedEntity = entityManager.merge(entity);
            log.debug("{} persisted", managedEntity);
            return managedEntity;
        } else {
            entityManager.persist(entity);
            log.debug("{} persisted", entity);
            return entity;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> Optional<E> findById(@Nonnull Class<E> entityClass, @Nonnull ID entityId) {
        checkNotNull(entityClass);
        checkNotNull(entityId);
        EntityManager entityManager = entityManagerProvider.get();
        E result = entityManager.find(entityClass, entityId);
        return (result == null) ? Optional.empty() : Optional.of(result);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public <E extends KrailEntity<ID, VER>> Optional<E> delete(@Nonnull E entity) {
        checkNotNull(entity);
        EntityManager entityManager = entityManagerProvider.get();
        E mergedEntity = merge(entity);
        if (mergedEntity.getId() != null) {
            //noinspection unchecked
            return deleteById((Class<E>) entity.getClass(), mergedEntity.getId());
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> Optional<E> deleteById(@Nonnull Class<E> entityClass, @Nonnull ID entityId) {
        checkNotNull(entityClass);
        checkNotNull(entityId);
        EntityManager entityManager = entityManagerProvider.get();
        Optional<E> entity = findById(entityClass, entityId);
        if (entity.isPresent()) {
            entityManager.remove(entity.get());
            log.debug("Deleted entity {}", entity);
        } else {
            log.debug("Attempting delete, but no entity found in {} for id {}", entityClass, entityId);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> E merge(@Nonnull E entity) {
        checkNotNull(entity);
        EntityManager entityManager = entityManagerProvider.get();
        E mergedEntity = entityManager.merge(entity);
        log.debug("{} merged", entity);
        return mergedEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        EntityManager entityManager = entityManagerProvider.get();
        EntityManagerImpl em = (EntityManagerImpl) entityManager;
        TypedQuery<E> query = em.createQuery("SELECT e FROM " + tableName(entityClass) + " e", entityClass);
        query.setFlushMode(FlushModeType.AUTO);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> String tableName(@Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        EntityManager entityManager = entityManagerProvider.get();
        EntityManagerImpl em = (EntityManagerImpl) entityManager;
        Metamodel meta = em.getMetamodel();
        EntityType<E> entityType = meta.entity(entityClass);

        //Check whether @Table annotation is present on the class.
        Table t = entityClass.getAnnotation(Table.class);

        //If no Table annotation use the default (simple class name)
        return (t == null) ? entityType.getName() : t.name();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> VER getVersion(@Nonnull E entity) {
        return entity.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public <E extends KrailEntity<ID, VER>> ID getIdentity(@Nonnull E entity) {
        return entity.getId();
    }


    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public <E extends KrailEntity<ID, VER>> long count(@Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        EntityManager entityManager = entityManagerProvider.get();
        String tableName = tableName(entityClass);
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM Widget c");
        return (Long) query.getSingleResult();
    }

    @Override
    public String connectionUrl() {
        EntityManager entityManager = entityManagerProvider.get();
        EntityManagerImpl em = (EntityManagerImpl) entityManager;
        return (String) em.getProperties()
                          .get(PersistenceUnitProperties.JDBC_URL);
    }

    public EntityManager getEntityManager() {
        return entityManagerProvider.get();
    }
}
