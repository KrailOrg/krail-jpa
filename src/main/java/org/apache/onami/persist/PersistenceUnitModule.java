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

package org.apache.onami.persist;

import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.util.Providers;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.option.jpa.DefaultJpaContainerProvider;
import uk.q3c.krail.core.option.jpa.DefaultJpaOptionContainerProvider;
import uk.q3c.krail.core.option.jpa.JpaContainerProvider;
import uk.q3c.krail.core.option.jpa.JpaOptionContainerProvider;
import uk.q3c.krail.i18n.persist.PatternDao;
import uk.q3c.krail.option.persist.OptionContainerProvider;
import uk.q3c.krail.option.persist.OptionDaoDelegate;
import uk.q3c.krail.persist.VaadinContainerProvider;

import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.lang.annotation.Annotation;
import java.util.Properties;

import static org.apache.onami.persist.Preconditions.*;

/**
 * Module for configuring a single persistence unit.
 *
 * @see PersistenceModule
 */
@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class PersistenceUnitModule extends PrivateModule {
    private static Logger log = LoggerFactory.getLogger(PersistenceUnitModule.class);

    /**
     * The configuration for the persistence unit.
     */
    private final PersistenceUnitModuleConfiguration config;

    /**
     * Transaction interceptor for this persistence unit.
     */
    private final TxnInterceptor transactionInterceptor;

    /**
     * Container for adding this persistence unit.
     */
    private final AllPersistenceUnits allPersistenceUnits;
    private MapBinder<Class<? extends Annotation>, Provider<OptionDaoDelegate>> optionDaoProviders;
    private MapBinder<Class<? extends Annotation>, Provider<PatternDao>> patternDaoProviders;

    /**
     * Constructor.
     *
     * @param configurator
     *         the configuration holding all configs.
     * @param transactionInterceptor
     *         interceptor for the transactional annotation.
     * @param allPersistenceUnits
     *         container holding all persistence units.
     */
    PersistenceUnitModule(PersistenceUnitModuleConfiguration configurator, TxnInterceptor transactionInterceptor, AllPersistenceUnits allPersistenceUnits) {
        this.config = checkNotNull(configurator, "config is mandatory!");
        this.transactionInterceptor = checkNotNull(transactionInterceptor, "transactionInterceptor is mandatory!");
        this.allPersistenceUnits = checkNotNull(allPersistenceUnits, "allPersistenceUnits is mandatory!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {


        bind(AnnotationHolder.class).toInstance(config.getAnnotationHolder());

        bindPersistenceServiceAndEntityManagerFactoryProviderAndProperties();
        bindTransactionFacadeFactory();

        bind(EntityManagerProvider.class).to(EntityManagerProviderImpl.class);
        bind(UnitOfWork.class).to(EntityManagerProviderImpl.class);

        exposePersistenceServiceAndEntityManagerProviderAndUnitOfWork();

        // request injection into transaction interceptor - this adds the required dependencies to the interceptor.
        if (transactionInterceptor != null) {
            requestInjection(transactionInterceptor);
        }

        allPersistenceUnits.add(getPersistenceKey(), getUnitOfWorkKey());
    }

    /**
     * exposes the following interfaces (optionally annotated if an annotation is defined in the configuration), along with any contained in the configuration
     * object
     * <ul>
     * <li>{@link PersistenceService}</li>
     * <li>{@link EntityManagerProvider}</li>
     * <li>{@link UnitOfWork}</li>
     * </ul>
     */
    private void exposePersistenceServiceAndEntityManagerProviderAndUnitOfWork() {
        if (config.isAnnotated()) {


            bindAndExposedAnnotated(PersistenceService.class);
            bindAndExposedAnnotated(EntityManagerProvider.class);
            bindAndExposedAnnotated(UnitOfWork.class);

            //bind additional interfaces to their implementations, but do not annotate & expose yet
            for (BindingPair bindingPair : config.getAdditionalBindings()) {
                bind(bindingPair.getInterfaceClass()).to(Key.get(bindingPair.getImplementationClass()));
            }

            bind(JpaContainerProvider.class).to(DefaultJpaContainerProvider.class);
            bind(VaadinContainerProvider.class).to(JpaContainerProvider.class);
            bind(OptionContainerProvider.class).to(JpaOptionContainerProvider.class);
            bind(JpaOptionContainerProvider.class).to(DefaultJpaOptionContainerProvider.class);


            //now bind the interfaces to annotation & expose
            for (BindingPair bindingPair : config.getAdditionalBindings()) {
                bindAndExposedAnnotated(bindingPair.getInterfaceClass());
            }

            bindAndExposedAnnotated(VaadinContainerProvider.class);
            bindAndExposedAnnotated(JpaContainerProvider.class);
            bindAndExposedAnnotated(OptionContainerProvider.class);

        } else {
            expose(PersistenceService.class);
            expose(EntityManagerProvider.class);
            expose(UnitOfWork.class);

            //bind additional interfaces to their implementations, but do not expose yet
            for (BindingPair bindingPair : config.getAdditionalBindings()) {
                bind(bindingPair.getInterfaceClass()).to(Key.get(bindingPair.getImplementationClass()));
            }

            //now expose the interfaces
            for (BindingPair bindingPair : config.getAdditionalBindings()) {
                expose(bindingPair.getInterfaceClass());
            }

        }
    }


    /**
     * helper to expose a binding with annotation added.
     *
     * @param type
     *         the type to expose.
     * @param <T>
     *         the type to expose.
     */
    private <T> void bindAndExposedAnnotated(Class<T> type) {
        bind(type).annotatedWith(config.getAnnotation())
                  .to(Key.get(type));
        expose(type).annotatedWith(config.getAnnotation());
    }


    private Key<PersistenceService> getPersistenceKey() {
        if (config.isAnnotated()) {
            return Key.get(PersistenceService.class, config.getAnnotation());
        } else {
            return Key.get(PersistenceService.class);
        }
    }

    private Key<UnitOfWork> getUnitOfWorkKey() {
        if (config.isAnnotated()) {
            return Key.get(UnitOfWork.class, config.getAnnotation());
        } else {
            return Key.get(UnitOfWork.class);
        }
    }

    private void bindPersistenceServiceAndEntityManagerFactoryProviderAndProperties() {
        if (config.isApplicationManagedPersistenceUnit()) {
            bindApplicationManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties();
        } else {
            bindContainerManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties();
        }
    }

    private void bindApplicationManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties() {
        bind(PersistenceService.class).to(ApplicationManagedEntityManagerFactoryProvider.class);
        bind(EntityManagerFactoryProvider.class).to(ApplicationManagedEntityManagerFactoryProvider.class);
        bind(Properties.class).annotatedWith(ForContainerManaged.class)
                .toProvider(Providers.of(null));
        bind(Properties.class).annotatedWith(ForApplicationManaged.class)
                              .toProvider(Providers.of(config.getProperties()));

        // required in ApplicationManagedEntityManagerFactoryProvider
        bind(EntityManagerFactoryFactory.class);
        // required in EntityManagerFactoryFactory
        bind(String.class).annotatedWith(ForApplicationManaged.class)
                          .toInstance(config.getPuName());
    }

    private void bindContainerManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties() {
        bind(PersistenceService.class).to(ContainerManagedEntityManagerFactoryProvider.class);
        bind(EntityManagerFactoryProvider.class).to(ContainerManagedEntityManagerFactoryProvider.class);
        bind(Properties.class).annotatedWith(ForContainerManaged.class)
                              .toProvider(Providers.of(config.getProperties()));
        bind(Properties.class).annotatedWith(ForApplicationManaged.class)
                .toProvider(Providers.of(null));

        // required in ContainerManagedEntityManagerFactoryProvider
        bindEntityManagerFactorySource();
    }

    private void bindEntityManagerFactorySource() {
        if (config.isEmfProvidedByJndiLookup()) {
            bind(EntityManagerFactorySource.class).to(EntityManagerFactorySourceByJndiLookup.class);

            // required in EntityManagerFactorySourceByJndiLookup
            bind(String.class).annotatedWith(ForContainerManaged.class)
                              .toInstance(config.getEmfJndiName());
        } else {
            bind(EntityManagerFactorySource.class).to(EntityManagerFactorySourceViaProvider.class);

            // required in EntityManagerFactorySourceViaProvider
            bindInternalEntityManagerFactoryProvider();
        }
    }

    private void bindInternalEntityManagerFactoryProvider() {
        if (config.isEmfProvidedByInstance()) {
            bind(EntityManagerFactory.class).annotatedWith(ForContainerManaged.class)
                                            .toInstance(config.getEmf());
        } else if (config.isEmfProvidedByProvider()) {
            bind(EntityManagerFactory.class).annotatedWith(ForContainerManaged.class)
                                            .toProvider(Providers.guicify(config.getEmfProvider()));
        } else if (config.isEmfProvidedByProviderKey()) {
            bind(EntityManagerFactory.class).annotatedWith(ForContainerManaged.class)
                                            .toProvider(config.getEmfProviderKey());
        } else {
            throw new RuntimeException("EntityManager is improperly configured");
        }
    }

    private void bindTransactionFacadeFactory() {
        if (config.isJta()) {
            bindJtaTransactionFacadeFactory();
        } else {
            bind(TransactionFacadeFactory.class).to(ResourceLocalTransactionFacadeFactory.class);
        }
    }

    private void bindJtaTransactionFacadeFactory() {
        bind(TransactionFacadeFactory.class).to(JtaTransactionFacadeFactory.class);

        // required in JtaTransactionFacadeFactory
        binInternalUserTransactionProvider();
    }

    private void binInternalUserTransactionProvider() {
        if (config.isUserTransactionProvidedByInstance()) {
            bind(UserTransaction.class).toInstance(config.getUserTransaction());
        } else if (config.isUserTransactionProvidedByJndiLookup()) {
            bind(UserTransaction.class).toProvider(UserTransactionProviderByJndiLookup.class);

            // required in UserTransactionProviderByJndiLookup
            bind(String.class).annotatedWith(UserTransactionJndiName.class)
                              .toInstance(config.getUtJndiName());
        } else if (config.isUserTransactionProvidedByProvider()) {
            bind(UserTransaction.class).toProvider(Providers.guicify(config.getUtProvider()));
        } else if (config.isUserTransactionProvidedByProviderKey()) {
            bind(UserTransaction.class).toProvider(config.getUtProviderKey());
        } else {
            throw new RuntimeException("UserTransaction is improperly configured");
        }
    }

}
