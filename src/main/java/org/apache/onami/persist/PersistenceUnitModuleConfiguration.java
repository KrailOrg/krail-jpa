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

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import javax.inject.Provider;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class holding the configuration for a single persistence unit.
 */
public class PersistenceUnitModuleConfiguration implements UnannotatedPersistenceUnitBuilder, AnnotatedPersistenceUnitBuilder,
        UnconfiguredPersistenceUnitBuilder {

    private List<BindingPair<?>> additionalBindings = new ArrayList<>();
    private Class<? extends Annotation> annotation;
    private EntityManagerFactory emf;
    private String emfJndiName;
    private Provider<EntityManagerFactory> emfProvider;
    private Key<? extends Provider<EntityManagerFactory>> emfProviderKey;
    private boolean isJta = false;
    private Properties properties;
    private boolean providesOptionDao;
    private boolean providesPatternDao;
    private String puName;
    private UserTransaction userTransaction;
    private String utJndiName;
    private Provider<UserTransaction> utProvider;
    private Key<? extends Provider<UserTransaction>> utProviderKey;
    public PersistenceUnitModuleConfiguration() {

    }

    public boolean providesPatternDao() {
        return providesPatternDao;
    }

    public boolean providesOptionDao() {
        return providesOptionDao;
    }

    /**
     * Adds a interface-implementation pair which will be bound in the {@link PersistenceUnitModule}, and attached to an annotation if {@link #annotation} is
     * not null
     *
     * @return this
     */
    public PersistenceUnitModuleConfiguration addPrivateBinding(BindingPair<?> bindingPair) {
        additionalBindings.add(bindingPair);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AnnotatedPersistenceUnitBuilder annotatedWith(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useLocalTransaction() {
        isJta = false;
        return this;
    }

    public List<BindingPair<?>> getAdditionalBindings() {
        return additionalBindings;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransaction(UserTransaction userTransaction) {
        this.isJta = true;

        this.userTransaction = userTransaction;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionWithJndiName(String utJndiName) {
        this.isJta = true;
        this.utJndiName = utJndiName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(Provider<UserTransaction> utProvider) {
        this.isJta = true;
        this.utProvider = utProvider;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(Class<? extends Provider<UserTransaction>> utProviderClass) {
        return useGlobalTransactionProvidedBy(Key.get(utProviderClass));
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(Key<? extends Provider<UserTransaction>> utProviderKey) {
        this.isJta = true;
        this.utProviderKey = utProviderKey;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(TypeLiteral<? extends Provider<UserTransaction>> utProviderType) {
        return useGlobalTransactionProvidedBy(Key.get(utProviderType));
    }

    void setEmfProviderClass(Class<? extends Provider<EntityManagerFactory>> emfProviderClass) {
        this.emfProviderKey = Key.get(emfProviderClass);
    }

    void setEmfProviderType(TypeLiteral<? extends Provider<EntityManagerFactory>> emfProviderType) {
        this.emfProviderKey = Key.get(emfProviderType);
    }

    boolean isApplicationManagedPersistenceUnit() {
        return puName != null;
    }

    UserTransaction getUserTransaction() {
        return userTransaction;
    }

    String getUtJndiName() {
        return utJndiName;
    }

    Provider<UserTransaction> getUtProvider() {
        return utProvider;
    }

    Key<? extends Provider<UserTransaction>> getUtProviderKey() {
        return utProviderKey;
    }

    Properties getProperties() {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    String getPuName() {
        return puName;
    }

    void setPuName(String puName) {
        this.puName = puName;
    }

    EntityManagerFactory getEmf() {
        return emf;
    }

    void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    String getEmfJndiName() {
        return emfJndiName;
    }

    void setEmfJndiName(String emfJndiName) {
        this.emfJndiName = emfJndiName;
    }

    Provider<EntityManagerFactory> getEmfProvider() {
        return emfProvider;
    }

    void setEmfProvider(Provider<EntityManagerFactory> emfProvider) {
        this.emfProvider = emfProvider;
    }

    Key<? extends Provider<EntityManagerFactory>> getEmfProviderKey() {
        return emfProviderKey;
    }

    void setEmfProviderKey(Key<? extends Provider<EntityManagerFactory>> emfProviderKey) {
        this.emfProviderKey = emfProviderKey;
    }

    boolean isEmfProvidedByJndiLookup() {
        return emfJndiName != null;
    }

    boolean isEmfProvidedByInstance() {
        return emf != null;
    }

    boolean isEmfProvidedByProvider() {
        return emfProvider != null;
    }

    boolean isEmfProvidedByProviderKey() {
        return emfProviderKey != null;
    }

    boolean isJta() {
        return isJta;
    }

    boolean isUserTransactionProvidedByJndiLookup() {
        return utJndiName != null;
    }

    boolean isUserTransactionProvidedByInstance() {
        return userTransaction != null;
    }

    boolean isUserTransactionProvidedByProvider() {
        return utProvider != null;
    }

    boolean isUserTransactionProvidedByProviderKey() {
        return utProviderKey != null;
    }

    boolean isAnnotated() {
        return annotation != null;
    }

    AnnotationHolder getAnnotationHolder() {
        return new AnnotationHolder(annotation);
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public void provideOptionDao(boolean provides) {
        this.providesOptionDao = provides;
    }

    public void providePatternDao(boolean provides) {
        this.providesPatternDao = provides;
    }
}
