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

package uk.q3c.krail.persist.jpa.i18n;

import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceUnitModule;
import uk.q3c.krail.core.persist.cache.i18n.PatternCacheKey;
import uk.q3c.krail.persist.jpa.common.BaseJpaKeyValueDao;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * The default implementation of {@link JpaPatternDao}.  The {@code entityManagerProvider} and {@code dao} are bound by {@link PersistenceUnitModule} to the
 * annotation which is used in the injection of this class. (For example, if an instance of this class is annotated with @Jpa1, then the constructor parameters
 * will also be bound with @Jpa1)
 * <p>
 * Created by David Sowerby on 15/04/15.
 */
public class DefaultJpaPatternDao extends BaseJpaKeyValueDao<PatternId, PatternCacheKey, JpaPatternEntity> implements JpaPatternDao {


    @Inject
    protected DefaultJpaPatternDao(EntityManagerProvider entityManagerProvider) {
        super(entityManagerProvider, JpaPatternEntity.class);
    }


    @Override
    protected JpaPatternEntity newEntity(PatternCacheKey cacheKey, String value) {
        return new JpaPatternEntity(cacheKey, value);
    }

    @Override
    protected PatternId newId(PatternCacheKey cacheKey) {
        return new PatternId(cacheKey);
    }


    @Nonnull
    @Override
    public Optional<String> getValue(@Nonnull PatternCacheKey cacheKey) {
        return super.getValue(cacheKey);
    }
}
