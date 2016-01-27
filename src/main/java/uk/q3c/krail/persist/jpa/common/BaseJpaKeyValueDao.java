/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.persist.jpa.common;

import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.Transactional;
import uk.q3c.krail.persist.jpa.i18n.KeyValueEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A common base class for Pattern and Option DAOs, which both require a key-value type of implementation
 * <p>
 * Created by David Sowerby on 18 Jan 2016
 */
public abstract class BaseJpaKeyValueDao<ID, K, E extends KeyValueEntity<ID, Integer>> extends BaseJpaDao<ID, Integer> implements JpaKeyValueDao<ID, Integer, K,
        E> {

    private Class<E> entityClass;

    public BaseJpaKeyValueDao(EntityManagerProvider entityManagerProvider, Class<E> entityClass) {
        super(entityManagerProvider);
        this.entityClass = entityClass;
    }

    @Transactional
    @Override
    public E write(@Nonnull K cacheKey, @Nonnull String value) {
        checkNotNull(cacheKey);
        checkNotNull(value);
        E existingEntity = find(cacheKey);
        if (existingEntity != null) {
            existingEntity.setValue(value);
            return save(existingEntity);
        } else {
            return save(newEntity(cacheKey, value));
        }
    }

    protected abstract E newEntity(K cacheKey, String value);

    protected abstract ID newId(K cacheKey);

    @Transactional
    @Override
    @Nullable
    public E find(@Nonnull K cacheKey) {
        checkNotNull(cacheKey);
        return getEntityManager().find(entityClass, newId(cacheKey));
    }

    @Transactional
    @Nonnull
    @Override
    public Optional<String> deleteValue(@Nonnull K cacheKey) {
        checkNotNull(cacheKey);
        Optional<E> deletedItem = deleteById(entityClass, newId(cacheKey));
        if (deletedItem.isPresent()) {
            return Optional.of(deletedItem.get()
                                          .getValue());
        } else {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Optional<String> getValueAsString(@Nonnull K cacheKey) {
        checkNotNull(cacheKey);
        E entity = find(cacheKey);
        return (entity == null) ? Optional.empty() : Optional.of(entity.getValue());
    }

    /**
     * returns the number of entries
     *
     * @return the number of entries
     */
    @Override
    @Transactional
    public long count() {
        return super.count(entityClass);
    }

}
