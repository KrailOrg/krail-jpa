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
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.CachingBatchableLocalEntityProvider;
import org.apache.onami.persist.EntityManagerProvider;
import uk.q3c.krail.core.persist.ContainerType;

/**
 * Created by David Sowerby on 03/01/15.
 */
public class DefaultJpaContainerProvider implements JpaContainerProvider {

    private EntityManagerProvider entityManagerProvider;

    @Inject
    protected DefaultJpaContainerProvider(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }


    @Override
    public <E> JPAContainer<E> get(Class<E> entityClass, ContainerType containerType) {

        EntityProvider<E> containerProvider = null;


        switch (containerType) {
            case CACHED:
                containerProvider = new CachingBatchableLocalEntityProvider<E>(entityClass);
                break;
        }
        containerProvider.setEntityManager(entityManagerProvider.get());
        JPAContainer container = new JPAContainer(entityClass);
        container.setEntityProvider(containerProvider);
        return container;
    }
}
