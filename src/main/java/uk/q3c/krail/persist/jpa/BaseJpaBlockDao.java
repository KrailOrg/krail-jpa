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
import uk.q3c.krail.core.data.BlockDao;
import uk.q3c.krail.core.data.Dao;
import uk.q3c.krail.core.data.KrailEntity;
import uk.q3c.krail.core.data.StatementBlock;
import uk.q3c.krail.util.QualityReview1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * A JPA implementation of {@link Dao} which delegates to a {@link JpaDao} for access to data.  Calling any method externally(except {@link #transact
 * (JpaStatementBlock)} will fail unless entityManager has been set, and is prepared for a transaction  The methods need to be public so that they can be
 * called from within a {@link StatementBlock}
 *
 * All calls which delegate to {@link Dao} do not use Precondition checks, as these are carried out by {@link Dao}
 *
 * Sub-classes are need to define the types of ID & VER.
 * <p>
 * Created by David Sowerby on 30/12/14.
 */

@QualityReview1
public abstract class BaseJpaBlockDao<ID, VER> implements JpaBlockDao<ID, VER> {
    private static Logger log = LoggerFactory.getLogger(BaseJpaBlockDao.class);
    private JpaDao<ID, VER> dao;
    private EntityManager entityManager;
    private EntityManagerProvider entityManagerProvider;


    protected BaseJpaBlockDao(EntityManagerProvider entityManagerProvider, JpaDao<ID, VER> dao) {
        super();
        this.entityManagerProvider = entityManagerProvider;
        this.dao = dao;
    }


    /**
     * @see Dao#save(Object, KrailEntity)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> E save(@Nonnull E entity) {
        return dao.save(entityManager, entity);
    }

    /**
     * @see BlockDao#transact(StatementBlock)
     */
    @Override
    @Transactional
    public void transact(@Nonnull JpaStatementBlock statementBlock) {
        log.debug("transaction opened");
        entityManager = entityManagerProvider.get();
        statementBlock.transact(this);
        log.debug("closing transaction");
    }

    /**
     * @see Dao#getIdentity(Object, KrailEntity)
     */
    @Nullable
    @Override
    public <E extends KrailEntity<ID, VER>> ID getIdentity(@Nonnull E entity) {
        return dao.getIdentity(entityManager, entity);
    }

    /**
     * @see #findById(Class, Object)
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
        return dao.findById(entityManager, entityClass, id);
    }

    /**
     *
     * @see Dao#count(Object, Class)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> long count(@Nonnull Class<E> entityClass) {
        return dao.count(entityManager, entityClass);
    }

    /**
     * @see Dao#findAll(Object, Class)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull Class<E> entityClass) {
        return dao.findAll(entityManager, entityClass);
    }

    /**
     * @see Dao#tableName(Object, Class)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> String tableName(@Nonnull Class<E> entityClass) {
        return dao.tableName(entityManager, entityClass);

    }

    /**
     * @see Dao#getVersion(Object, KrailEntity)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> VER getVersion(@Nonnull E entity) {
        return entity.getVersion();
    }

    /**
     * @see Dao#delete(Object, KrailEntity)
     */
    @Override
    public <E extends KrailEntity<ID, VER>> void delete(@Nonnull E entity) {
        dao.delete(entityManager, entity);
    }

    /**
     * @see Dao#merge(Object, KrailEntity)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> E merge(@Nonnull E entity) {
        return dao.merge(entityManager, entity);
    }

    /**
     * @see Dao#deleteById(Object, Class, Object)
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<ID, VER>> Optional<E> deleteById(@Nonnull Class<E> entityClass, @Nonnull ID entityId) {
        return dao.deleteById(entityManager, entityClass, entityId);
    }


    /**
     * @see Dao#connectionUrl(Object)
     */
    @Override
    public String connectionUrl() {
        return dao.connectionUrl(entityManager);
    }

}
