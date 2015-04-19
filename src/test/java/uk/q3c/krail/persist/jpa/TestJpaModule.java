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

import org.apache.onami.persist.PersistenceUnitModuleConfiguration;
import org.junit.rules.TemporaryFolder;
import uk.q3c.krail.core.user.opt.jpa.BaseJpaOptionDao;
import uk.q3c.krail.core.user.opt.jpa.JpaOptionDao;
import uk.q3c.krail.i18n.jpa.DefaultJpaPatternDao;
import uk.q3c.krail.i18n.jpa.JpaPatternDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by David Sowerby on 03/01/15.
 */
public class TestJpaModule extends JpaModule {

    private final TemporaryFolder tempFolder;

    public TestJpaModule() {
        tempFolder = new TemporaryFolder();
        try {
            tempFolder.create();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp folder in TestJpaUnitModule", e);
        }
    }

    /**
     * Override this with calls to {@link #bindApplicationManagedPersistenceUnit(String)} ofr testing outside the container
     *
     * @param annotation
     * @param entityManagerFactory
     *
     * @return
     */
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

    /**
     * Configures the persistence units over the exposed methods.
     */
    @Override
    protected void define() {
        addPersistenceUnit("derbyDb", Jpa1.class, derbyConfig());
        addPersistenceUnit("hsqlDb", Jpa2.class, hsqlConfig());
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
              .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE)
              .addPrivateBinding(StandardTestEntityJpaSpecificDao.class, DefaultStandardTestEntityJpaSpecificDao.class)
              .
                      addPrivateBinding(JpaOptionDao.class, BaseJpaOptionDao.class)
              .addPrivateBinding(JpaPatternDao.class, DefaultJpaPatternDao.class);

        return config;
    }

    private DefaultJpaInstanceConfiguration hsqlConfig() {
        DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
        config.db(JpaDb.HSQLDB)
              .autoCreate(true)
              .url("mem:test")
              .user("sa")
              .password("")
              .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);
        return config;
    }
}
