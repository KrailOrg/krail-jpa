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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.Transactional;
import uk.q3c.krail.core.data.Select;
import uk.q3c.krail.core.data.StringPersistenceConverter;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.krail.core.user.opt.cache.DefaultOptionCacheLoader;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.persist.jpa.DefaultJpaDao_LongInt;

import javax.annotation.Nonnull;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static uk.q3c.krail.core.data.Select.Compare.EQ;
import static uk.q3c.krail.core.user.profile.RankOption.SPECIFIC_RANK;

/**
 * Converts {@link OptionCacheKey} to {@link OptionEntity_LongInt} for persistence.  Sub-class and construct with the appropriately annotated {@code dao} and
 * {@code
 * EntityManagerProvider}
 * <br>
 * <b>NOTE:</b> All values to and from {@link Option} are natively typed.  All values to and from {@link OptionCache}, {@link DefaultOptionCacheLoader} and
 * {@link OptionDao} are wrapped in Optional.
 * <br>
 * Created by David Sowerby on 13/04/15.
 */
public class DefaultOptionJpaDao_LongInt extends DefaultJpaDao_LongInt implements OptionJpaDao_LongInt {


    private StringPersistenceConverter stringPersistenceConverter;

    @Inject
    protected DefaultOptionJpaDao_LongInt(EntityManagerProvider entityManagerProvider, StringPersistenceConverter stringPersistenceConverter) {
        super(entityManagerProvider);
        this.stringPersistenceConverter = stringPersistenceConverter;
    }

    @Override
    public <V> void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Optional<V> value) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        //noinspection ConstantConditions

        final OptionEntity_LongInt entity = new OptionEntity_LongInt(cacheKey, stringPersistenceConverter.convertToPersistence(value)
                                                                                         .get());
        save(entity);
    }


    @Transactional
    @Nonnull
    @Override
    public Optional<?> deleteValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        final Optional<OptionEntity_LongInt> entity = find(cacheKey);
        if (entity.isPresent()) {
            String entityValue = entity.get()
                                       .getValue();
            delete(entity.get());
            return Optional.of(entityValue);
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    @Nonnull
    public Optional<OptionEntity_LongInt> find(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);

        Select select = selectSingleRank(cacheKey);

        TypedQuery<OptionEntity_LongInt> query = getEntityManager().createQuery(select.toString(), OptionEntity_LongInt.class);
        List<OptionEntity_LongInt> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    protected Select selectSingleRank(@Nonnull OptionCacheKey cacheKey) {
        return new Select().from(tableName(OptionEntity_LongInt.class))
                           .where("userHierarchyName", EQ, cacheKey.getHierarchy()
                                                                   .persistenceName())
                           .and("rankName", EQ, cacheKey.getRequestedRankName())
                           .and("optionKey", EQ, cacheKey.getOptionKey()
                                                         .compositeKey());
    }

    @Nonnull
    @Override
    public Optional<?> getHighestRankedValue(@Nonnull final OptionCacheKey cacheKey) {
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
    protected Optional<?> findFirstRankedValue(@Nonnull final OptionCacheKey cacheKey, @Nonnull List<String> ranks) {
        Optional<?> value = Optional.empty();
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
    public Optional<?> getValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        final Optional<OptionEntity_LongInt> optionalEntity = find(cacheKey);
        if (optionalEntity.isPresent()) {

            String value = optionalEntity.get()
                                         .getValue();

            return stringPersistenceConverter.convertFromPersistence(cacheKey, value);
        } else {
            return Optional.empty();
        }
    }


    @Nonnull
    @Override
    public Optional<?> getLowestRankedValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.LOWEST_RANK);
        final ImmutableList<String> ranks = cacheKey.getHierarchy()
                                                    .ranksForCurrentUser()
                                                    .reverse();
        return findFirstRankedValue(cacheKey, ranks);
    }


    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public int clear() {
        final Query query = getEntityManager().createQuery("DELETE FROM " + tableName(OptionEntity_LongInt.class));
        return query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return super.count(OptionEntity_LongInt.class);
    }


}
