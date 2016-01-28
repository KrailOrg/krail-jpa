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

package uk.q3c.krail.persist.jpa.option;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceUnitModule;
import org.apache.onami.persist.Transactional;
import uk.q3c.krail.core.data.OptionStringConverter;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.option.OptionException;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheLoader;
import uk.q3c.krail.core.persist.cache.option.OptionCache;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.persist.common.option.OptionDao;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.persist.jpa.common.BaseJpaKeyValueDao;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
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
public class DefaultJpaOptionDao extends BaseJpaKeyValueDao<JpaOptionId, OptionCacheKey, JpaOptionEntity> implements JpaOptionDao {


    private OptionStringConverter optionStringConverter;

    @Inject
    protected DefaultJpaOptionDao(EntityManagerProvider entityManagerProvider, OptionStringConverter optionStringConverter) {
        super(entityManagerProvider, JpaOptionEntity.class);
        this.optionStringConverter = optionStringConverter;
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
    public <V> void write(@Nonnull OptionCacheKey<V> cacheKey, @Nonnull Optional<V> value) {
        checkRankOption(cacheKey, SPECIFIC_RANK);
        checkArgument(value.isPresent(), "Value must be present");
        String stringValue = optionStringConverter.convertValueToString(value.get());
        write(cacheKey, stringValue);
    }

    @Nonnull
    @Override
    @Transactional
    public <V> Optional<V> getValue(@Nonnull OptionCacheKey<V> cacheKey) {
        Optional<String> optionalStringValue;

        switch (cacheKey.getRankOption()) {
            case HIGHEST_RANK:
                optionalStringValue = getRankedValue(cacheKey, false);
                break;
            case LOWEST_RANK:
                optionalStringValue = getRankedValue(cacheKey, true);
                break;
            case SPECIFIC_RANK:
                optionalStringValue = getStringValue(cacheKey);
                break;
            default:
                throw new OptionException("Unrecognised rankOption");
        }
        return optionalStringValue.isPresent() ? Optional.of(optionStringConverter.convertStringToValue(cacheKey, optionalStringValue.get())) : Optional
                .empty();
    }

    @Nonnull
    @Override
    public Optional<String> deleteValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.SPECIFIC_RANK);
        return super.deleteValue(cacheKey);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    protected <V> Optional<String> getRankedValue(@Nonnull OptionCacheKey<V> cacheKey, boolean lowest) {
        ImmutableList<String> ranks = cacheKey.getHierarchy()
                                              .ranksForCurrentUser();
        ImmutableList<String> ranksToUse = (lowest) ? ranks.reverse() : ranks;
        for (String rank : ranksToUse) {
            OptionCacheKey<V> specificKey = new OptionCacheKey<>(cacheKey, rank, RankOption.SPECIFIC_RANK);
            Optional<String> stringValue = getStringValue(specificKey);
            if (stringValue.isPresent()) return stringValue;
        }
        return Optional.empty();
    }

    protected Optional<String> getStringValue(@Nonnull OptionCacheKey<?> cacheKey) {
        EntityManager entityManager = getEntityManager();
        JpaOptionEntity jpaOptionEntity = entityManager.find(JpaOptionEntity.class, new JpaOptionId(cacheKey));
        if (jpaOptionEntity == null) {
            return Optional.empty();
        } else {
            return Optional.of(jpaOptionEntity.getValue());
        }
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
    protected JpaOptionId newId(OptionCacheKey cacheKey) {
        return new JpaOptionId(cacheKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return super.count(JpaOptionEntity.class);
    }


}
