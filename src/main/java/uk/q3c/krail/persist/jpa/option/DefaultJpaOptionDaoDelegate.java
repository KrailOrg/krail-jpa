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

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceUnitModule;
import org.apache.onami.persist.Transactional;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.persist.OptionCache;
import uk.q3c.krail.option.persist.OptionCacheKey;
import uk.q3c.krail.option.persist.OptionDaoDelegate;
import uk.q3c.krail.option.persist.cache.DefaultOptionCacheLoader;
import uk.q3c.krail.persist.jpa.common.BaseJpaKeyValueDao;
import uk.q3c.util.data.DataConverter;

import javax.annotation.Nonnull;

/**
 * Converts {@link OptionCacheKey} to {@link JpaOptionEntity} for persistence.
 *
 * Injected automatically with the correct {@link EntityManagerProvider} (where correct == annotated the same as this instance).  This is done by the {@link
 * PersistenceUnitModule}
 * <br>
 * <b>NOTE:</b> All values to and from {@link Option} are natively typed.  All values to and from {@link OptionCache}, {@link DefaultOptionCacheLoader} and
 * {@link OptionDaoDelegate} are wrapped in Optional.
 * <br>
 * Created by David Sowerby on 13/04/15.
 */
public class DefaultJpaOptionDaoDelegate extends BaseJpaKeyValueDao<JpaOptionId, OptionCacheKey, JpaOptionEntity> implements JpaOptionDaoDelegate {


    private DataConverter dataConverter;

    @SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL") //injected
    @Inject
    protected DefaultJpaOptionDaoDelegate(EntityManagerProvider entityManagerProvider, DataConverter dataConverter) {
        super(entityManagerProvider, JpaOptionEntity.class);
        this.dataConverter = dataConverter;
    }


    @Override
    public <V> void write(@Nonnull OptionCacheKey<V> cacheKey, @Nonnull String value) {
        super.write(cacheKey, value);

    }


    /**
     * {@inheritDoc}
     */
    @SuppressFBWarnings("SQL_INJECTION_JPA")//entityName() is final and can only return SimpleClassName of annotation
    @SuppressWarnings("JpaQlInspection")
    @Transactional
    @Override
    public long clear() {
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
