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

package uk.q3c.krail.jpa.user.opt;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceUnitModule;
import org.apache.onami.persist.Transactional;
import uk.q3c.krail.core.data.OptionStringConverter;
import uk.q3c.krail.core.data.Select;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.krail.core.user.opt.cache.DefaultOptionCacheLoader;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.jpa.persist.BaseJpaKeyValueDao;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.q3c.krail.core.data.Select.Compare.EQ;
import static uk.q3c.krail.core.user.profile.RankOption.SPECIFIC_RANK;

/**
 * Converts {@link OptionCacheKey} to {@link JpaOptionEntity} for persistence.
 *
 * Injected automatically with the correct {@link EntityManagerProvider} (where correct == annotated the same as this instance).  This is done by the {@link
 * PersistenceUnitModule}
 * <br>
 * <b>NOTE:</b> All values to and from {@link Option} are natively typed.  All values to and from {@link OptionCache}, {@link DefaultOptionCacheLoader} and
 * {@link OptionDao} are wrapped in Optional.
 * <br>
 * Created by David Sowerby on 13/04/15.
 */
public class DefaultJpaOptionDao extends BaseJpaKeyValueDao<OptionId, OptionCacheKey, JpaOptionEntity> implements JpaOptionDao {


    private OptionStringConverter optionStringConverter;

    @Inject
    protected DefaultJpaOptionDao(EntityManagerProvider entityManagerProvider, OptionStringConverter optionStringConverter) {
        super(entityManagerProvider, JpaOptionEntity.class);
        this.optionStringConverter = optionStringConverter;
    }


    protected <V> Select selectSingleRank(@Nonnull OptionCacheKey<V> cacheKey) {
        return new Select().from(entityName(JpaOptionEntity.class))
                           .where("userHierarchyName", EQ, cacheKey.getHierarchy()
                                                                   .persistenceName())
                           .and("rankName", EQ, cacheKey.getRequestedRankName())
                           .and("optionKey", EQ, cacheKey.getOptionKey()
                                                         .compositeKey());
    }

    @Nonnull
    @Override
    public <V> Optional<?> getHighestRankedValue(@Nonnull final OptionCacheKey<V> cacheKey) {
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
    protected <V> Optional<?> findFirstRankedValue(@Nonnull final OptionCacheKey<V> cacheKey, @Nonnull List<String> ranks) {
        Optional<?> value = Optional.empty();
        for (String rank : ranks) {
            OptionCacheKey<V> searchKey = new OptionCacheKey<>(cacheKey, rank, SPECIFIC_RANK);
            value = getValue(searchKey);
            if (value.isPresent()) {
                return value;
            }
        }
        return value;
    }

    @Override
    public <V> Object write(@Nonnull OptionCacheKey<V> cacheKey, @Nonnull Optional<V> value) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        checkArgument(value.isPresent(), "Value must be present");
        String stringValue = optionStringConverter.convertValueToString(value.get());
        return write(cacheKey, stringValue);
    }

    @Nonnull
    @Override
    public <V> Optional<?> getValue(@Nonnull OptionCacheKey<V> cacheKey) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        Optional<String> v = getValueAsString(cacheKey);
        return (v.isPresent()) ? Optional.of(optionStringConverter.convertStringToValue(cacheKey, v.get())) : Optional.empty();
    }


    @Nonnull
    @Override
    public <V> Optional<?> getLowestRankedValue(@Nonnull OptionCacheKey<V> cacheKey) {
        checkRankOption(cacheKey, RankOption.LOWEST_RANK);
        final ImmutableList<String> ranks = cacheKey.getHierarchy()
                                                    .ranksForCurrentUser()
                                                    .reverse();
        return findFirstRankedValue(cacheKey, ranks);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressFBWarnings("SQL_INJECTION_JPA")//entityName() is final and can only return SimpleClassName of annotation
    @SuppressWarnings("JpaQlInspection")
    @Transactional
    @Override
    public int clear() {
        return getEntityManager().createQuery("DELETE FROM " + entityName(JpaOptionEntity.class))
                                 .executeUpdate();
    }

    @Override
    protected JpaOptionEntity newEntity(OptionCacheKey cacheKey, String value) {
        return new JpaOptionEntity(cacheKey, value);
    }

    @Override
    protected OptionId newId(OptionCacheKey cacheKey) {
        return new OptionId(cacheKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return super.count(JpaOptionEntity.class);
    }


}
