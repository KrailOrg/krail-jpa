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

package uk.q3c.krail.persist.jpa.option

import com.google.inject.Inject
import org.apache.onami.persist.EntityManagerProvider
import org.apache.onami.persist.PersistenceService
import org.apache.onami.persist.UnitOfWork
import org.eclipse.persistence.config.PersistenceUnitProperties
import spock.guice.UseModules
import testutil.TestOptionModule
import uk.q3c.krail.core.data.DataModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.persist.common.option.DefaultOptionDao
import uk.q3c.krail.core.persist.common.option.OptionDaoDelegate
import uk.q3c.krail.core.persist.common.option.OptionDaoTestBase
import uk.q3c.krail.persist.jpa.common.Jpa1

/**
 * Created by David Sowerby on 21 Jan 2016
 */
@UseModules([TestOptionJpaModule, DataModule, TestOptionModule, VaadinSessionScopeModule])
class DefaultJpaOptionDaoDelegateTest extends OptionDaoTestBase {
    @Inject
    @Jpa1
    PersistenceService persistenceService;

    @Inject
    @Jpa1
    OptionDaoDelegate injectedDaoDelegate;

    @Inject
    @Jpa1
    EntityManagerProvider entityManagerProvider

    @Inject
    @Jpa1
    UnitOfWork unitOfWork;

    def setup() {
        optionSource.getActiveDao() >> injectedDaoDelegate
        persistenceService.start();
        unitOfWork.begin();
        dao = new DefaultOptionDao(optionElementConverter, optionSource)
        expectedConnectionUrl = (String) entityManagerProvider.get().getProperties()
                .get(PersistenceUnitProperties.JDBC_URL)
    }

    def cleanup() {
        unitOfWork.end();
        persistenceService.stop();
    }

}
