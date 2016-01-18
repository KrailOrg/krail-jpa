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

package uk.q3c.krail.jpa.persist;

import org.apache.onami.persist.Transactional;
import uk.q3c.krail.jpa.i18n.KeyValueEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Created by David Sowerby on 18 Jan 2016
 */
public interface JpaKeyValueDao<ID, VER, K, E extends KeyValueEntity> extends JpaDao<ID, VER> {
    @Transactional
    E write(@Nonnull K cacheKey, @Nonnull String value);

    @Transactional
    @Nullable
    E find(@Nonnull K cacheKey);

    @Transactional
    @Nonnull
    Optional<String> deleteValue(@Nonnull K cacheKey);

    @Nonnull
    Optional<String> getValueAsString(@Nonnull K cacheKey);

    @Transactional
    long count();
}
