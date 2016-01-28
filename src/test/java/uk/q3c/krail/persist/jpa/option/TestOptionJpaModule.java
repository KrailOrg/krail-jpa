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

package uk.q3c.krail.persist.jpa.option;

import org.apache.onami.persist.PersistenceUnitModuleConfiguration;
import org.junit.rules.TemporaryFolder;
import uk.q3c.krail.core.persist.common.option.OptionDao;
import uk.q3c.krail.persist.jpa.common.DefaultJpaInstanceConfiguration;
import uk.q3c.krail.persist.jpa.common.Jpa1;
import uk.q3c.krail.persist.jpa.common.JpaDb;
import uk.q3c.krail.persist.jpa.common.JpaModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Sets up Jpa for testing {@link OptionDao}
 * <p>
 * Created by David Sowerby on 10/07/15.
 */
public class TestOptionJpaModule extends JpaModule {


    private final TemporaryFolder tempFolder;

    public TestOptionJpaModule() {
        tempFolder = new TemporaryFolder();
        try {
            tempFolder.create();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp folder in TestAppJpaUnitModule", e);
        }
    }

    @Override
    protected void define() {
        addPersistenceUnit("derbyDb", Jpa1.class, derbyConfig());
    }

    private DefaultJpaInstanceConfiguration derbyConfig() {
        DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
        File dbFolder = new File(tempFolder.getRoot(), "derbyDb");

        config.transactionType(DefaultJpaInstanceConfiguration.TransactionType.RESOURCE_LOCAL)
              .db(JpaDb.DERBY_EMBEDDED)
              .autoCreate(true)
              .url(dbFolder.getAbsolutePath())
              .user("test")
              .password("test")
              .provideOptionDao()
              .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);

        return config;
    }

    @Override
    protected PersistenceUnitModuleConfiguration bindPU(@Nonnull String puName, @Nullable Class<? extends Annotation> annotation, EntityManagerFactory
            entityManagerFactory) {
        PersistenceUnitModuleConfiguration conf;
        if (annotation == null) {
            conf = (PersistenceUnitModuleConfiguration) bindApplicationManagedPersistenceUnit(puName);
        } else {
            conf = (PersistenceUnitModuleConfiguration) bindApplicationManagedPersistenceUnit(puName).annotatedWith(annotation);
        }
        return conf;
    }
}
