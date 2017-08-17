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

package uk.q3c.krail.core.option.jpa;

import com.google.inject.Inject;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.CachingBatchableLocalEntityProvider;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.UnitOfWork;
import uk.q3c.krail.persist.ContainerType;
import uk.q3c.krail.persist.PersistenceConfigurationException;
import uk.q3c.krail.persist.jpa.common.EntityManagerProviderOnamiWrapper;

/**
 * Sets up and returns a JPAContainer instance for use with Onami persistence.  An instance of this should be injected, annotated for the appropriate
 * persistence unit.
 * <p>
 * <p>
 * Created by David Sowerby on 03/01/15.
 */
public class DefaultJpaContainerProvider implements JpaContainerProvider {

    private EntityManagerProvider entityManagerProvider;
    private UnitOfWork unitOfWork;

    @Inject
    protected DefaultJpaContainerProvider(EntityManagerProvider entityManagerProvider, UnitOfWork unitOfWork) {
        this.entityManagerProvider = entityManagerProvider;
        this.unitOfWork = unitOfWork;
    }


    @Override
    public <E> JPAContainer<E> get(Class<E> entityClass, ContainerType containerType) {

        EntityProvider<E> containerProvider = null;

        // TODO Other provider types
        //https://github.com/davidsowerby/krail-jpa/issues/9
        switch (containerType) {
            case CACHED:
                containerProvider = new CachingBatchableLocalEntityProvider<>(entityClass);
                break;
            default:
                throw new PersistenceConfigurationException("Unrecognised Container Type");
        }
        containerProvider.setEntityManagerProvider(new EntityManagerProviderOnamiWrapper(entityManagerProvider, unitOfWork));
        JPAContainer<E> container = new JPAContainer<>(entityClass);
        container.setEntityProvider(containerProvider);
        return container;
    }
}
