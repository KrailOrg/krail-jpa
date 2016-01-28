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

import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

/**
 * A wrapper to 'convert' the JPAContainer EntityManagerProvider to the Onami Persist  EntityManagerProvider.  Also ensures that the Unit of Work is running
 * when an {@link EntityManager} is returned.  This behaviour is similar, which starts the UnitOfWork when an EntityManager is returned.   Onami, however, does
 * not do that by default.
 * <p>
 * Created by David Sowerby on 06/07/15.
 */
public class EntityManagerProviderOnamiWrapper implements com.vaadin.addon.jpacontainer.EntityManagerProvider {

    private static Logger log = LoggerFactory.getLogger(EntityManagerProviderOnamiWrapper.class);
    private EntityManagerProvider entityManagerProvider;
    private UnitOfWork unitOfWork;

    public EntityManagerProviderOnamiWrapper(EntityManagerProvider entityManagerProvider, UnitOfWork unitOfWork) {

        this.entityManagerProvider = entityManagerProvider;
        this.unitOfWork = unitOfWork;
    }

    /**
     * Checks that the UnitOfWork is running and starts it if not, then returns the {@link EntityManager}
     *
     * @return the entity manager
     */
    @Override
    public EntityManager getEntityManager() {
        if (!unitOfWork.isActive()) {
            unitOfWork.begin();
            log.debug("Unit of Work started by EntityManagerProvider wrapper, thread id = {}", Thread.currentThread()
                                                                                                     .getId());
        } else {
            log.debug("Unit of Work already running, thread id = {}", Thread.currentThread()
                                                                            .getId());
        }
        return entityManagerProvider.get();
    }
}
