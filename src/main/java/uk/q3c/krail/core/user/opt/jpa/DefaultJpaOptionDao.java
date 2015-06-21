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

package uk.q3c.krail.core.user.opt.jpa;

import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.Transactional;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import uk.q3c.krail.core.data.Select;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.persist.jpa.StandardJpaStatementDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static uk.q3c.krail.core.data.Select.Compare.EQ;
import static uk.q3c.krail.core.user.profile.RankOption.SPECIFIC_RANK;

/**
 * Converts {@link OptionCacheKey} to {@link OptionEntity} for persistence.  Sub-class and construct with the appropriately annotated {@code dao} and {@code
 * EntityManagerProvider}
 * <p>
 * Created by David Sowerby on 13/04/15.
 */
public abstract class DefaultJpaOptionDao implements JpaOptionDao {


    private final StandardJpaStatementDao dao;
    private EntityManagerProvider entityManagerProvider;

    protected DefaultJpaOptionDao(StandardJpaStatementDao dao, EntityManagerProvider entityManagerProvider) {
        this.dao = dao;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Object value) {
        throw new UnsupportedOperationException("Not supported.  Use call with Converter, write(OptionCacheKey, Converter, value)");
    }

    @Override
    public <V> void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Converter<V, String> converter, @Nonnull V value) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        //noinspection ConstantConditions
        final OptionEntity entity = new OptionEntity(cacheKey, converter.convert(value));
        dao.save(entity);
    }


    @Nullable
    @Override
    public Object deleteValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        final Optional<OptionEntity> entity = find(cacheKey);
        if (entity.isPresent()) {
            dao.delete(entity.get());
            return entity.get()
                         .getValue();
        } else {
            return null;
        }
    }

    @Transactional
    @Nonnull
    public Optional<OptionEntity> find(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);

        Select select = selectSingleRank(cacheKey);

        // see https://github.com/davidsowerby/krail/issues/364
        EntityManagerImpl entityManager = (EntityManagerImpl) entityManagerProvider.get();
        TypedQuery<OptionEntity> query = entityManager
                                                              .createQuery(select.toString(), OptionEntity.class);
        List<OptionEntity> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    protected Select selectSingleRank(@Nonnull OptionCacheKey cacheKey) {
        return new Select().clazz(dao.tableName(OptionEntity.class))
                           .where("userHierarchyName", EQ, cacheKey.getHierarchy()
                                                                   .persistenceName())
                           .and("rankName", EQ, cacheKey.getRequestedRankName())
                           .and("optionKey", EQ, cacheKey.getOptionKey()
                                                         .compositeKey());
    }

    @Nonnull
    @Override
    public Optional<Object> getHighestRankedValue(@Nonnull final OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.HIGHEST_RANK);
        final ImmutableList<String> ranks = cacheKey.getHierarchy()
                                                    .ranksForCurrentUser();

        return findFirstRankedValue(cacheKey, ranks);
    }

    /**
     * Returns the first value found from the ordered {@code ranks}
     *
     * @param cacheKey
     *         the key to identify candidates (the rank name is replaced by a member of ranks)
     * @param ranks
     *         the ranks to look for
     *
     * @return the first value found, or Optional.empty() if none found
     */
    protected Optional<Object> findFirstRankedValue(@Nonnull final OptionCacheKey cacheKey, @Nonnull List<String> ranks) {
        Optional<Object> value = Optional.empty();
        for (String rank : ranks) {
            OptionCacheKey searchKey = new OptionCacheKey(cacheKey, rank, SPECIFIC_RANK);
            value = getValue(searchKey);
            if (value.isPresent()) {
                return value;
            }
        }
        return value;
    }

    @Nonnull
    @Override
    public Optional<Object> getValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        final Optional<OptionEntity> entity = find(cacheKey);
        if (entity.isPresent()) {
            return Optional.of(entity.get()
                                     .getValue());
        } else {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public <V> Optional<V> getHighestRankedValue(@Nonnull Converter<String, V> converter, @Nonnull final OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.HIGHEST_RANK);
        final ImmutableList<String> ranks = cacheKey.getHierarchy()
                                                    .ranksForCurrentUser();

        return findFirstRankedValue(converter, cacheKey, ranks);
    }

    protected <V> Optional<V> findFirstRankedValue(@Nonnull Converter<String, V> converter, @Nonnull final OptionCacheKey cacheKey, @Nonnull List<String>
            ranks) {
        Optional<V> value = Optional.empty();
        for (String rank : ranks) {
            OptionCacheKey searchKey = new OptionCacheKey(cacheKey, rank, SPECIFIC_RANK);
            value = getValue(converter, searchKey);
            if (value.isPresent()) {
                return value;
            }
        }
        return value;
    }

    @Nonnull
    @Override
    public <V> Optional<V> getValue(@Nonnull Converter<String, V> converter, @Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        checkNotNull(converter);
        final Optional<OptionEntity> entity = find(cacheKey);
        if (entity.isPresent()) {
            V result = converter.convert(entity.get()
                                               .getValue());
            if (result != null) {
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<Object> getLowestRankedValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.LOWEST_RANK);
        final ImmutableList<String> ranks = cacheKey.getHierarchy()
                                                    .ranksForCurrentUser()
                                                    .reverse();
        return findFirstRankedValue(cacheKey, ranks);
    }


    @Nonnull
    @Override
    public <V> Optional<V> getLowestRankedValue(@Nonnull Converter<String, V> converter, @Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.LOWEST_RANK);
        final ImmutableList<String> ranks = cacheKey.getHierarchy()
                                                    .ranksForCurrentUser()
                                                    .reverse();
        return findFirstRankedValue(converter, cacheKey, ranks);
    }

    @Override
    public String connectionUrl() {
        return dao.connectionUrl();
    }


}
