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

package uk.q3c.krail.jpa.user.opt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceUnitModule;
import org.apache.onami.persist.Transactional;
import uk.q3c.krail.core.data.OptionStringConverter;
import uk.q3c.krail.core.data.Select;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.krail.core.user.opt.OptionException;
import uk.q3c.krail.core.user.opt.cache.DefaultOptionCacheLoader;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.jpa.persist.DefaultJpaDao_LongInt;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static uk.q3c.krail.core.data.Select.Compare.EQ;
import static uk.q3c.krail.core.user.profile.RankOption.SPECIFIC_RANK;

/**
 * Converts {@link OptionCacheKey} to {@link OptionEntity_LongInt} for persistence.
 *
 * Injected automatically with the correct {@link EntityManagerProvider} (where correct == annotated the same as this instance).  This is done by the {@link
 * PersistenceUnitModule}
 * <br>
 * <b>NOTE:</b> All values to and from {@link Option} are natively typed.  All values to and from {@link OptionCache}, {@link DefaultOptionCacheLoader} and
 * {@link OptionDao} are wrapped in Optional.
 * <br>
 * Created by David Sowerby on 13/04/15.
 */
public class DefaultOptionJpaDao_LongInt extends DefaultJpaDao_LongInt implements OptionJpaDao_LongInt {


    private OptionStringConverter optionStringConverter;

    @Inject
    protected DefaultOptionJpaDao_LongInt(EntityManagerProvider entityManagerProvider, OptionStringConverter optionStringConverter) {
        super(entityManagerProvider);
        this.optionStringConverter = optionStringConverter;
    }

    @Override
    @Transactional
    @Nonnull
    public <V> Object write(@Nonnull OptionCacheKey cacheKey, @Nonnull Optional<V> value) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        Preconditions.checkArgument(value.isPresent(), "Value must be non-empty");
        EntityManager entityManager = getEntityManager();
        // is there an existing entity (bearing in mind that the id field is not the same as the key field
        String stringValue = optionStringConverter.convertValueToString(value.get());


        Optional<OptionEntity_LongInt> existingEntity = find(cacheKey);
        if (existingEntity.isPresent()) {
            existingEntity.get()
                          .setValue(stringValue);
            entityManager.persist(existingEntity.get());
            return existingEntity.get();
        } else {
            //noinspection ConstantConditions
            final OptionEntity_LongInt entity = new OptionEntity_LongInt(cacheKey, stringValue);
            entityManager.persist(entity);
            return entity;
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
            if (results.size() > 1) {
                throw new OptionException("Multiple values for one cache key found, cacheKey =  " + cacheKey.toString());
            }
            return Optional.of(results.get(0));
        }
    }

    protected Select selectSingleRank(@Nonnull OptionCacheKey cacheKey) {
        return new Select().from(entityName(OptionEntity_LongInt.class))
                           .where("userHierarchyName", EQ, cacheKey.getHierarchy()
                                                                   .persistenceName())
                           .and("rankName", EQ, cacheKey.getRequestedRankName())
                           .and("optionKey", EQ, cacheKey.getOptionKey()
                                                         .compositeKey());
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

            return Optional.of(optionStringConverter.convertStringToValue(cacheKey, value));
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
        final Query query = getEntityManager().createQuery("DELETE FROM " + entityName(OptionEntity_LongInt.class));
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
