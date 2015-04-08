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

package org.apache.onami.persist;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Factory for {@link EntityManagerFactory}.
 */
@Singleton
public class EntityManagerFactoryFactory {

    /**
     * Name of the persistence unit as defined in the persistence.xml.
     */
    private final String puName;

    /**
     * Additional properties. Theses override the ones defined in the persistence.xml.
     */
    private final Properties properties;

    /**
     * Constructor.
     *
     * @param puName
     *         the name of the persistence unit as defined in the persistence.xml. Must not be {@code null}.
     * @param properties
     *         the additional properties. Theses override the ones defined in the persistence.xml.
     *         Must not be {@code null}.
     */
    @Inject
    EntityManagerFactoryFactory(@ForApplicationManaged String puName, @Nullable @ForApplicationManaged Properties properties) {
        this.puName = checkNotNull(puName, "puName is mandatory!");
        this.properties = properties;

    }

    /**
     * Creates a new {@link EntityManagerFactory}.
     *
     * @return the newly created entity manager factory.
     */
    public EntityManagerFactory createApplicationManagedEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(puName, properties);
    }

}
