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
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.persist.jpa.i18n.LabelKey;
import uk.q3c.krail.service.AbstractService;
import uk.q3c.krail.service.RelatedServiceExecutor;
import uk.q3c.krail.service.Service;
import uk.q3c.krail.service.ServiceMonitor;
import uk.q3c.util.guice.SerializationSupport;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * NOT CURRENTLY USED, see https://github.com/davidsowerby/krail-jpa/issues/6
 * <p>
 * <p>
 * Default implementation for {@link JpaService}.  Wraps the Apache Onami {@link PersistenceService} in a Krail {@link Service} wrapper, so that the service is
 * monitored by {@link ServiceMonitor}, and automatically stopped when the application is closed down
 * <p>
 * Created by David Sowerby on 21/06/15.
 */
public class DefaultJpaService extends AbstractService implements JpaService, Service {

    private final transient PersistenceService persistenceService;

    @Inject
    protected DefaultJpaService(Translate translate, PersistenceService persistenceService, MessageBus messageBus, RelatedServiceExecutor servicesExecutor, SerializationSupport serializationSupport) {
        super(translate, messageBus, servicesExecutor, serializationSupport);
        this.persistenceService = persistenceService;
    }

    @Override
    protected void doStop() {
        persistenceService.stop();
    }

    @Override
    protected void doStart() {
        persistenceService.start();
    }

    @Override
    public I18NKey getNameKey() {
        return LabelKey.JPA_Service;
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();
        getSerializationSupport().deserialize(this);
    }
}