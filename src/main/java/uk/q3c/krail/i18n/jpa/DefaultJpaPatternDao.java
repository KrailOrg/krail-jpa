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
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import uk.q3c.krail.core.data.Select;
import uk.q3c.krail.i18n.PatternCacheKey;
import uk.q3c.krail.persist.jpa.StandardJpaStatementDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The default implementation of {@link JpaPatternDao}.  The {@code entityManagerProvider} and {@code dao} are bound by {@link PersistenceUnitModule} to the
 * annotation which is used in the injection of this class. (For example, if an instance of this class is annotated with @Jpa1, then the constructor arameters
 * will also be bound with @Jpa1)
 * <p>
 * Created by David Sowerby on 15/04/15.
 */
public class DefaultJpaPatternDao implements JpaPatternDao {


    private final StandardJpaStatementDao dao;
    private EntityManagerProvider entityManagerProvider;

    @Inject
    protected DefaultJpaPatternDao(EntityManagerProvider entityManagerProvider, StandardJpaStatementDao dao) {
        this.dao = dao;
        this.entityManagerProvider = entityManagerProvider;
    }


    @Override
    public void write(@Nonnull PatternCacheKey cacheKey, @Nonnull String value) {
        checkNotNull(cacheKey);
        checkNotNull(value);
        PatternEntity patternEntity = new PatternEntity(cacheKey, value);
        dao.save(patternEntity);
    }


    @Nullable
    @Override
    public String deleteValue(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        Optional<PatternEntity> entity = find(cacheKey);
        if (entity.isPresent()) {
            dao.delete(entity.get());
            return entity.get()
                         .getValue();
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    @Nonnull
    public Optional<PatternEntity> find(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        Select select = selectEntity(cacheKey);


        // see https://github.com/davidsowerby/krail/issues/364
        EntityManagerImpl entityManager = (EntityManagerImpl) entityManagerProvider.get();
        TypedQuery<PatternEntity> query = entityManager
                                                               .createQuery(select.toString(), PatternEntity.class);
        List<PatternEntity> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    protected Select selectEntity(PatternCacheKey cacheKey) {
        //ensure the same conversion as writing
        PatternEntity searchKey = new PatternEntity(cacheKey, "x");
        return new Select().clazz(dao.tableName(PatternEntity.class))
                           .where("i18nkey", searchKey.getI18nkey())
                           .and("locale", searchKey.getLocale());
    }

    @Nonnull
    @Override
    public Optional<String> getValue(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        Optional<PatternEntity> entity = find(cacheKey);
        if (entity.isPresent()) {
            return Optional.of(entity.get()
                                     .getValue());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String connectionUrl() {
        return dao.connectionUrl();
    }
}
