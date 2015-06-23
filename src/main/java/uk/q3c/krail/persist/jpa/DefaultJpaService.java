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
import org.apache.onami.persist.PersistenceService;
import uk.q3c.krail.core.services.AbstractServiceI18N;
import uk.q3c.krail.core.services.Service;
import uk.q3c.krail.core.services.ServicesMonitor;
import uk.q3c.krail.i18n.Translate;

/**
 * NOT CURRENTLY USED, see https://github.com/davidsowerby/krail-jpa/issues/6
 * <p>
 * <p>
 * Default implementation for {@link JpaService}.  Wraps the Apache Onami {@link PersistenceService} in a Krail {@link Service} wrapper, so that the service is
 * monitored by {@link ServicesMonitor}, and automatically stopped when the application is closed down
 * <p>
 * Created by David Sowerby on 21/06/15.
 */
public class DefaultJpaService extends AbstractServiceI18N implements JpaService {

    private final PersistenceService persistenceService;

    @Inject
    protected DefaultJpaService(Translate translate, PersistenceService persistenceService) {
        super(translate);
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
}