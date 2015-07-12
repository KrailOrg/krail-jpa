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

package uk.q3c.krail.i18n.jpa;

import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceUnitModule;
import org.apache.onami.persist.Transactional;
import uk.q3c.krail.core.data.Select;
import uk.q3c.krail.i18n.PatternCacheKey;
import uk.q3c.krail.persist.jpa.DefaultJpaDao_LongInt;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The default implementation of {@link PatternJpaDao}.  The {@code entityManagerProvider} and {@code dao} are bound by {@link PersistenceUnitModule} to the
 * annotation which is used in the injection of this class. (For example, if an instance of this class is annotated with @Jpa1, then the constructor parameters
 * will also be bound with @Jpa1)
 * <p>
 * Created by David Sowerby on 15/04/15.
 */
public class DefaultPatternJpaDao_LongInt extends DefaultJpaDao_LongInt implements PatternJpaDao {

    @Inject
    protected DefaultPatternJpaDao_LongInt(EntityManagerProvider entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Transactional
    @Override
    public void write(@Nonnull PatternCacheKey cacheKey, @Nonnull String value) {
        checkNotNull(cacheKey);
        checkNotNull(value);
        Optional<PatternEntity_LongInt> existingValue = find(cacheKey);
        if (existingValue.isPresent()) {
            existingValue.get()
                         .setValue(value);
            save(existingValue.get());
        } else {
            save(keyToEntity(cacheKey, value));
        }
    }

    @Nonnull
    protected PatternEntity_LongInt keyToEntity(@Nonnull PatternCacheKey key, @Nonnull String value) {
        return new PatternEntity_LongInt(key, value);
    }

    @Transactional
    @Override
    @Nonnull
    public Optional<PatternEntity_LongInt> find(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        Select select = selectEntity(cacheKey);

        TypedQuery<PatternEntity_LongInt> query = getEntityManager().createQuery(select.toString(), PatternEntity_LongInt.class);
        List<PatternEntity_LongInt> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    protected Select selectEntity(PatternCacheKey cacheKey) {
        //ensure the same conversion as writing
        PatternEntity_LongInt searchKey = new PatternEntity_LongInt(cacheKey, "x");
        return new Select().from(tableName(PatternEntity_LongInt.class))
                           .where("i18nkey", searchKey.getI18nkey())
                           .and("locale", searchKey.getLocale());
    }

    @Transactional
    @Nonnull
    @Override
    public Optional<String> deleteValue(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);


        TypedQuery<PatternEntity_LongInt> query = getEntityManager().createQuery(selectFromKey(cacheKey), PatternEntity_LongInt.class);
        List<PatternEntity_LongInt> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            final Optional<PatternEntity_LongInt> patternEntity = deleteById(PatternEntity_LongInt.class, results.get(0)
                                                                                                 .getId());
            if (patternEntity.isPresent()) {
                return Optional.of(patternEntity.get()
                                                .getValue());
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Returns a Select String from the {@code cacheKey}.  Uses {@link PatternEntity_LongInt} to ensure conversion from key to entity is consistent for the esearch
     * compared to the original write
     *
     * @param cacheKey
     *         the key being selected
     *
     * @return a Select String from the {@code cacheKey}
     */
    protected String selectFromKey(@Nonnull PatternCacheKey cacheKey) {
        PatternEntity_LongInt searchPattern = keyToEntity(cacheKey, "x");
        return new Select().from(PatternEntity_LongInt.class)
                           .where("i18nkey", searchPattern.getI18nkey())
                           .and("locale", searchPattern.getLocale())
                           .toString();
    }

    @Nonnull
    @Override
    public Optional<String> getValue(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        Optional<PatternEntity_LongInt> entity = find(cacheKey);
        if (entity.isPresent()) {
            return Optional.of(entity.get()
                                     .getValue());
        } else {
            return Optional.empty();
        }
    }

    /**
     * returns the number of entries
     *
     * @return the number of entries
     */
    @Override
    public long count() {
        return super.count(PatternEntity_LongInt.class);
    }


}
