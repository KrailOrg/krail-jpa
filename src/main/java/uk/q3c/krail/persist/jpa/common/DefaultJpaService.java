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

package uk.q3c.krail.persist.jpa.common;

import com.google.inject.Inject;
import org.apache.onami.persist.PersistenceService;
import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.services.AbstractService;
import uk.q3c.krail.core.services.RelatedServicesExecutor;
import uk.q3c.krail.core.services.Service;
import uk.q3c.krail.core.services.ServicesMonitor;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.persist.jpa.i18n.LabelKey;

/**
 * NOT CURRENTLY USED, see https://github.com/davidsowerby/krail-jpa/issues/6
 * <p>
 * <p>
 * Default implementation for {@link JpaService}.  Wraps the Apache Onami {@link PersistenceService} in a Krail {@link Service} wrapper, so that the service is
 * monitored by {@link ServicesMonitor}, and automatically stopped when the application is closed down
 * <p>
 * Created by David Sowerby on 21/06/15.
 */
public class DefaultJpaService extends AbstractService implements JpaService, Service {

    private final PersistenceService persistenceService;

    @Inject
    protected DefaultJpaService(Translate translate, PersistenceService persistenceService, GlobalBusProvider globalBusProvider,
                                RelatedServicesExecutor servicesExecutor) {
        super(translate, globalBusProvider, servicesExecutor);
        this.persistenceService = persistenceService;
    }

    @Override
    protected void doStop() throws Exception {
        persistenceService.stop();
    }

    @Override
    protected void doStart() throws Exception {
        persistenceService.start();
    }

    @Override
    public I18NKey getNameKey() {
        return LabelKey.JPA_Service;
    }
}