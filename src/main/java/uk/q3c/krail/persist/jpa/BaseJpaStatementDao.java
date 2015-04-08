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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.data.Dao;
import uk.q3c.krail.core.data.KrailEntity;
import uk.q3c.krail.core.data.StatementDao;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * A JPA implementation of {@link StatementDao}.  The PersistenceUnitModule associates an instance of this with the correct EntityManagerProvider.  Sub-classes
 * provides implementations for specific ID and VER types
 * <p>
 * <p>
 * NOTE: delegates to {@link Dao}, so these methods do not need Preconditions checks
 * Created by David Sowerby on 30/12/14.
 */


public abstract class BaseJpaStatementDao<ID, VER> implements JpaStatementDao<ID, VER> {
    private static Logger log = LoggerFactory.getLogger(BaseJpaStatementDao.class);
    private JpaDao<ID, VER> baseDao;
    private EntityManagerProvider entityManagerProvider;

    protected BaseJpaStatementDao(EntityManagerProvider entityManagerProvider, JpaDao<ID, VER> baseDao) {
        super();
        this.entityManagerProvider = entityManagerProvider;
        this.baseDao = baseDao;
    }



    /**
     * @see Dao#save(Object, KrailEntity)
     */
    @Nonnull
    @Override
    @Transactional
    public <E extends KrailEntity<ID, VER>> E save(@Nonnull E entity) {
        return baseDao.save(entityManagerProvider.get(), entity);
    }

    /**
     * @see Dao#getIdentity(Object, KrailEntity)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> ID getIdentity(@Nonnull E entity) {
        return baseDao.getIdentity(entityManagerProvider.get(), entity);
    }

    /**
     * The same as {@link #findById(Class, Object)}
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> Optional<E> load(@Nonnull Class<E> entityClass, @Nonnull ID identity) {
        return findById(entityClass, identity);
    }

    /**
     * @see Dao#findById(Object, Class, Object)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> Optional<E> findById(@Nonnull Class<E> entityClass, @Nonnull ID id) {
        return baseDao.findById(entityManagerProvider.get(), entityClass, id);
    }

    /**
     * @param entityClass
     *
     * @see Dao#count(Object, Class)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> long count(@Nonnull Class<E> entityClass) {
        return baseDao.count(entityManagerProvider.get(), entityClass);
    }

    /**
     * @see Dao#findAll(Object, Class)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull Class<E> entityClass) {
        return baseDao.findAll(entityManagerProvider.get(), entityClass);
    }

    /**
     * @see Dao#tableName(Object, Class)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> String tableName(@Nonnull Class<E> entityClass) {
        return baseDao.tableName(entityManagerProvider.get(), entityClass);

    }

    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> VER getVersion(@Nonnull E entity) {
        return baseDao.getVersion(entityManagerProvider.get(), entity);
    }

    @Nonnull
    @Override
    @Transactional
    public <E extends KrailEntity<ID, VER>> void delete(@Nonnull E entity) {
        baseDao.delete(entityManagerProvider.get(), entity);
    }

    @Nonnull
    @Override
    @Transactional
    public <E extends KrailEntity<ID, VER>> E merge(@Nonnull E entity) {
        return baseDao.merge(entityManagerProvider.get(), entity);
    }

    @Nonnull
    @Override
    @Transactional
    public <E extends KrailEntity<ID, VER>> Optional<E> deleteById(@Nonnull Class<E> entityClass, @Nonnull ID entityId) {
        return baseDao.deleteById(entityManagerProvider.get(), entityClass, entityId);
    }

    /**
     * @see Dao#connectionUrl(Object)
     */
    @Override
    public String connectionUrl() {
        return baseDao.connectionUrl(entityManagerProvider.get());
    }
}
