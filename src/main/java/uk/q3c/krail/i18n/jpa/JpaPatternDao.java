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

import org.apache.onami.persist.Transactional;
import uk.q3c.krail.i18n.PatternCacheKey;
import uk.q3c.krail.i18n.PatternDao;

import java.util.Optional;

/**
 * JPA specific interface for {@link PatternDao} to enable binding alternatives
 * <p>
 * Created by David Sowerby on 15/04/15.
 */
public interface JpaPatternDao extends PatternDao {

    @Transactional
    Optional<PatternEntity> find(PatternCacheKey cacheKey);
}
