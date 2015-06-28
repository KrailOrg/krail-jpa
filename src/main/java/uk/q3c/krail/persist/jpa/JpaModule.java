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

import com.google.inject.BindingAnnotation;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.apache.onami.persist.PersistenceModule;
import org.apache.onami.persist.PersistenceUnitModuleConfiguration;
import uk.q3c.krail.core.persist.OptionDaoProviders;
import uk.q3c.krail.core.persist.PatternDaoProviders;
import uk.q3c.krail.core.user.opt.CoreDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.annotation.Annotation;

/**
 * Sets up {@link PersistenceModule} with some Krail specific extensions
 * <p>
 * Created by David Sowerby on 30/12/14.
 */
public abstract class JpaModule extends PersistenceModule {

    private Multibinder<Class<? extends Annotation>> optionDaoProviders;
    private Multibinder<Class<? extends Annotation>> patternDaoProviders;

    /**
     * Adds a single JPA persistence unit.  If you require multiple persistence units, an annotation is required in
     * order to identify different EntityManagers - use {@link #addPersistenceUnit(String, Class, JpaInstanceConfiguration)} instead.  You
     * may want to consider using an annotation even if you currently only need a single persistence unit - it would
     * be much easier then to add another later, while maintaining clarity of which persistence unit is used where.
     *
     * @param puName
     *         the persistence unit name - must be defined in persistence.xml
     * @param configuration
     *         a configuration object to supplement or override the values in persistence.xml.
     */
    protected void addPersistenceUnit(String puName, @Nonnull JpaInstanceConfiguration configuration) {
        addPersistenceUnit(puName, null, configuration);
    }

    /**
     * Adds a JPA persistence unit and associates it with an annotation - the annotation is required in order to
     * identify different EntityManagers if you are using multiple persistence units.  If you are using only one
     * persistence unit, you could also use {@link #addPersistenceUnit(String, JpaInstanceConfiguration)}
     *
     * @param puName
     *         the persistence unit name - must be defined in persistence.xml
     * @param annotation
     *         - the annotation class to associate the persistence unit to.  Must be a {@link BindingAnnotation}
     * @param configuration
     *         a configuration object to supplement or override the values in persistence.xml.
     */
    protected void addPersistenceUnit(String puName, Class<? extends Annotation> annotation, @Nonnull JpaInstanceConfiguration configuration) {
        EntityManagerFactory entityManagerFactory;
        entityManagerFactory = Persistence.createEntityManagerFactory(puName, configuration.toProperties());
        //bind factory for use with JPAContainer setup
        bind(EntityManagerFactory.class).annotatedWith(annotation)
                                        .toInstance(entityManagerFactory);

        PersistenceUnitModuleConfiguration conf = bindPU(puName, annotation, entityManagerFactory);
        //Transfer properties and additional bindings
        conf.setProperties(configuration.toProperties());
        conf.provideOptionDao(configuration.providesOptionDao());
        conf.providePatternDao(configuration.providesPatternDao());
        conf.getAdditionalBindings()
            .addAll(configuration.getAdditionalBindings());
        registerDaoProviders(conf);

    }

    protected void registerDaoProviders(PersistenceUnitModuleConfiguration config) {

        Class<? extends Annotation> annotation = (config.getAnnotation() == null) ? CoreDao.class : config.getAnnotation();

        if (config.providesPatternDao()) {
            patternDaoProviders.addBinding()
                               .toInstance(annotation);
        }

        if (config.providesOptionDao()) {
            optionDaoProviders.addBinding()
                              .toInstance(annotation);
        }
    }

    /**
     * Override this with calls to {@link #bindApplicationManagedPersistenceUnit(String)} for testing outside the container
     *
     * @param puName
     *         the persistence unit name - must be defined in persistence.xml
     * @param annotation
     *         - the annotation class to associate the persistence unit to.  Must be a {@link BindingAnnotation}
     * @param entityManagerFactory
     *
     * @return
     */
    protected PersistenceUnitModuleConfiguration bindPU(@Nonnull String puName, @Nullable Class<? extends Annotation> annotation, EntityManagerFactory
            entityManagerFactory) {
        PersistenceUnitModuleConfiguration conf;
        if (annotation == null) {
            conf = (PersistenceUnitModuleConfiguration) bindContainerManagedPersistenceUnit(entityManagerFactory);
        } else {
            conf = (PersistenceUnitModuleConfiguration) bindContainerManagedPersistenceUnit(entityManagerFactory).annotatedWith(annotation);
        }
        return conf;
    }

    /**
     * Configures the persistence units over the exposed methods.
     */
    @Override
    protected void configurePersistence() {
        TypeLiteral<Class<? extends Annotation>> annotationClassLiteral = new TypeLiteral<Class<? extends Annotation>>() {
        };

        patternDaoProviders = Multibinder.newSetBinder(binder(), annotationClassLiteral, PatternDaoProviders.class);
        optionDaoProviders = Multibinder.newSetBinder(binder(), annotationClassLiteral, OptionDaoProviders.class);

        bindInstanceConfiguration();
        bindContainerProvider();
        define();
    }


    /**
     * Override this method to provide your own {@link JpaContainerProvider} implementation
     */
    protected void bindContainerProvider() {
        bind(JpaContainerProvider.class).to(DefaultJpaContainerProvider.class);
    }


    protected abstract void define();

    protected void bindInstanceConfiguration() {
        bind(JpaInstanceConfiguration.class).to(DefaultJpaInstanceConfiguration.class);
    }


}
