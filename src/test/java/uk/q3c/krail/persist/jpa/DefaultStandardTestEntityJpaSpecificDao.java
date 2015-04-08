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

package uk.q3c.krail.persist.jpa;

import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;

/**
 * Created by David Sowerby on 12/04/15.
 */
public class DefaultStandardTestEntityJpaSpecificDao extends BaseJpaSpecificDao<StandardTestEntity, Long, Integer> implements StandardTestEntityJpaSpecificDao {

    //    @Inject
    //    protected DefaultStandardTestEntityJpaSpecificDao(EntityManagerProvider entityManagerProvider, Provider<StandardJpaDao> jpaDaoProvider) {
    //        super(entityManagerProvider,jpaDaoProvider);
    //    }

    @Inject
    protected DefaultStandardTestEntityJpaSpecificDao(EntityManagerProvider entityManagerProvider, StandardJpaDao jpaDao) {
        super(entityManagerProvider, jpaDao);
    }
}
