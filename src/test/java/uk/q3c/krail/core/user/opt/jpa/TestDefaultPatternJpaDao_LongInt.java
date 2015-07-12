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

import org.apache.onami.persist.EntityManagerProvider;
import uk.q3c.krail.core.data.KrailEntity;
import uk.q3c.krail.i18n.jpa.DefaultPatternJpaDao_LongInt;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

/**
 * The {@link #tableName(Class)} method causes issues when testing - the strange problem of having to cast to the {@link EntityManager} implementation causes
 * test failures
 * <p>
 * Created by David Sowerby on 10/07/15.
 */
public class TestDefaultPatternJpaDao_LongInt extends DefaultPatternJpaDao_LongInt {
    protected TestDefaultPatternJpaDao_LongInt(EntityManagerProvider entityManagerProvider) {
        super(entityManagerProvider);
    }

    /**
     * {@inheritDoc}
     *
     * @param entityClass
     */
    @Nonnull
    @Override
    public <E extends KrailEntity<Long, Integer>> String tableName(@Nonnull Class<E> entityClass) {
        return "Pattern";
    }
}
