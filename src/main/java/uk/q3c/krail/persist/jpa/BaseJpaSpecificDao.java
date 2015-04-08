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
import uk.q3c.krail.core.data.Dao;
import uk.q3c.krail.core.data.KrailEntity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * A base class for use with JPA Dao implementations which explicitly state the class they are used for, as opposed to the {@link JpaStatementDao}, which
 * provides CRUD and basic query operations for any JPA entity.  If you use this, you will need a sub-class for each {@link KrailEntity} type - using the
 * {@link JpaStatementDao} does not require that.
 * <p>
 * This implementation actually delegates to the {@link Dao} for most of its functionality
 * <p>
 * There is no need to explicitly set the entity class for this implementation, it is discovered by reflection.
 * <p>
 * Created by David Sowerby on 06/04/15.
 */
public abstract class BaseJpaSpecificDao<E extends KrailEntity<ID, VER>, ID, VER> implements JpaSpecificDao<E, ID, VER> {
    private Class<E> entityClass;
    private EntityManagerProvider entityManagerProvider;
    private JpaDao<ID, VER> jpaDao;


    public BaseJpaSpecificDao(EntityManagerProvider entityManagerProvider, JpaDao<ID, VER> jpaDao) {
        this.entityManagerProvider = entityManagerProvider;
        this.jpaDao = jpaDao;
        Type t = (getClass().getGenericSuperclass());

        //depending on the way the class is instantiated, the parameter information appears at a different level of the hierarchy
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            //noinspection unchecked
            entityClass = (Class<E>) pt.getActualTypeArguments()[0];
        } else {
            Type t1 = ((Class) t).getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) t1;
            //noinspection unchecked
            entityClass = (Class<E>) pt.getActualTypeArguments()[0];
        }
    }

    @Override
    @Transactional
    public List<E> findAll() {
        return jpaDao.findAll(entityManagerProvider.get(), entityClass);
    }

    @Override
    public Optional<E> load(ID identity) {
        return findById(identity);
    }

    @Override
    @Transactional
    public Optional<E> findById(ID entityId) {
        return jpaDao.findById(entityManagerProvider.get(), entityClass, entityId);
    }

    @Override
    @Transactional
    public Optional<E> deleteById(ID entityId) {
        return jpaDao.deleteById(entityManagerProvider.get(), entityClass, entityId);
    }


    @Override
    public String tableName() {
        return jpaDao.tableName(entityManagerProvider.get(), entityClass);
    }

    /**
     * Creates and returns a new instance of the entity class
     *
     * @return a new instance of the entity class
     */
    @Override
    public E newEntity() {
        try {
            final E entity = entityClass.newInstance();
            save(entity);
            return entity;
        } catch (Exception e) {
            throw new EntityException("Unable to create new entity for  " + entityClass.getName(), e);
        }
    }

    @Override
    public E save(E entity) {
        return jpaDao.save(entityManagerProvider.get(), entity);
    }


}
