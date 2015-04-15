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

import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceService;
import org.apache.onami.persist.UnitOfWork;
import org.junit.After;
import org.junit.Before;
import uk.q3c.krail.persist.jpa.Jpa1;
import uk.q3c.krail.persist.jpa.Jpa2;

public abstract class BaseJpaTest {
    @Inject
    @Jpa1
    protected EntityManagerProvider firstEmp;

    @Inject
    @Jpa2
    protected EntityManagerProvider secondEmp;

    @Inject
    @Jpa1
    protected PersistenceService persistenceService1;

    @Inject
    @Jpa2
    protected PersistenceService persistenceService2;

    @Inject
    @Jpa1
    protected UnitOfWork unitOfWork1;

    @Inject
    @Jpa2
    protected UnitOfWork unitOfWork2;

    @Before
    public void setUp() {
        persistenceService1.start();
        persistenceService2.start();
        unitOfWork1.begin();
        unitOfWork2.begin();
    }


    @After
    public void tearDown() throws Exception {
        unitOfWork1.end();
        unitOfWork2.end();
        persistenceService1.stop();
        persistenceService2.stop();
    }
}
