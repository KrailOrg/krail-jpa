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

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.data.Dao;
import uk.q3c.krail.core.data.KrailEntity;
import uk.q3c.krail.util.QualityReview1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation of {@link JpaDao}.  Sub-classs for specific ID and VER types
 * <p>
 * <p>
 * Created by David Sowerby on 08/04/15.
 */
@QualityReview1
public abstract class BaseJpaDao<ID, VER> implements JpaDao<ID, VER> {

    private static Logger log = LoggerFactory.getLogger(BaseJpaDao.class);

    /**
     * @see Dao#save(Object, KrailEntity)
     */
    @Nonnull
    public <E extends KrailEntity<ID, VER>> E save(@Nonnull EntityManager entityManager, @Nonnull E entity) {
        checkNotNull(entity);
        checkNotNull(entityManager);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            log.debug("{} persisted", entity);
            return entity;
        }


        @SuppressWarnings("unchecked") Optional<E> fEntity = findById(entityManager, (Class<E>) entity.getClass(), entity.getId());
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
     * @see Dao#findById(Object, Class, Object)
     */
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> Optional<E> findById(@Nonnull EntityManager entityManager, @Nonnull Class<E> entityClass, @Nonnull ID entityId) {
        checkNotNull(entityClass);
        checkNotNull(entityManager);
        checkNotNull(entityId);

        E result = entityManager.find(entityClass, entityId);
        return (result == null) ? Optional.empty() : Optional.of(result);
    }

    /**
     * @see Dao#delete(Object, KrailEntity)
     */
    @Override
    public <E extends KrailEntity<ID, VER>> void delete(@Nonnull EntityManager entityManager, @Nonnull E entity) {
        checkNotNull(entity);
        checkNotNull(entityManager);
        E mergedEntity = merge(entityManager, entity);
        if (mergedEntity.getId() != null) {
            deleteById(entityManager, entity.getClass(), mergedEntity.getId());
        }
    }

    /**
     * @see Dao#deleteById(Object, Class, Object)
     */
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> Optional<E> deleteById(@Nonnull EntityManager entityManager, @Nonnull Class<E> entityClass, @Nonnull ID entityId) {
        checkNotNull(entityClass);
        checkNotNull(entityManager);
        checkNotNull(entityId);
        Optional<E> entity = findById(entityManager, entityClass, entityId);
        if (entity.isPresent()) {
            entityManager.remove(entity.get());
            log.debug("Deleted entity {}", entity);
        } else {
            log.debug("Attempting delete, but no entity found in {} for id {}", entityClass, entityId);
        }
        return entity;
    }

    /**
     * @see Dao#merge(Object, KrailEntity)
     */
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> E merge(@Nonnull EntityManager entityManager, @Nonnull E entity) {
        checkNotNull(entityManager);
        checkNotNull(entity);
        E mergedEntity = entityManager.merge(entity);
        log.debug("{} merged", entity);
        return mergedEntity;
    }

    /**
     * @see Dao#findAll(Object, Class)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull EntityManager entityManager, @Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        checkNotNull(entityManager);
        EntityManagerImpl em = (EntityManagerImpl) entityManager;
        TypedQuery<E> query = em.createQuery("SELECT e FROM " + tableName(entityManager, entityClass) + " e", entityClass);
        query.setFlushMode(FlushModeType.AUTO);
        return query.getResultList();
    }

    /**
     * @see Dao#tableName(Object, Class)
     */
    @Override
    @Nonnull
    public <E extends KrailEntity<ID, VER>> String tableName(@Nonnull EntityManager entityManager, @Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        checkNotNull(entityManager);
        EntityManagerImpl em = (EntityManagerImpl) entityManager;
        Metamodel meta = em.getMetamodel();
        EntityType<E> entityType = meta.entity(entityClass);

        //Check whether @Table annotation is present on the class.
        Table t = entityClass.getAnnotation(Table.class);

        //If no Table annotation use the default (simple class name)
        return (t == null) ? entityType.getName() : t.name();
    }

    /**
     * @see Dao#getVersion(Object, KrailEntity)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> VER getVersion(@Nonnull EntityManager entityManager, @Nonnull E entity) {
        return entity.getVersion();
    }

    /**
     * @see Dao#getIdentity(Object, KrailEntity)
     */
    @Nullable
    @Override
    public <E extends KrailEntity<ID, VER>> ID getIdentity(@Nonnull EntityManager entityManager, @Nonnull E entity) {
        return entity.getId();
    }


    /**
     * @see Dao#count(Object, Class)
     */
    @Override
    public <E extends KrailEntity<ID, VER>> long count(@Nonnull EntityManager entityManager, @Nonnull Class<E> entityClass) {
        checkNotNull(entityClass);
        checkNotNull(entityManager);
        String tableName = tableName(entityManager, entityClass);
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM " + tableName + " c");
        return (Long) query.getSingleResult();
    }

    @Override
    public String connectionUrl(@Nonnull EntityManager entityManager) {
        EntityManagerImpl em = (EntityManagerImpl) entityManager;
        return (String) em.getProperties()
                          .get(PersistenceUnitProperties.JDBC_URL);
    }
}
