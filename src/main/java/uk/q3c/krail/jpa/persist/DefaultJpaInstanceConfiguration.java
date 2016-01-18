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

package uk.q3c.krail.jpa.persist;

import org.apache.onami.persist.BindingPair;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.PatternDao;
import uk.q3c.krail.jpa.i18n.DefaultJpaPatternDao;
import uk.q3c.krail.jpa.user.opt.DefaultJpaOptionDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Helper class to populate the equivalent of persistence.xml properties.  A persistence.xml file is still needed but
 * can be minimal
 * <p>
 * Created by David Sowerby on 01/01/15.
 */
public class DefaultJpaInstanceConfiguration extends HashMap<String, Object> implements JpaInstanceConfiguration<DefaultJpaInstanceConfiguration> {

    private List<BindingPair<?>> additionalBindings;
    private boolean autoCreate;
    private String connectionUrl;
    private JpaDb db;
    private I18NKey description;
    private I18NKey name;
    private boolean provideOptionDao = false;
    private boolean providePatternDao;
    private boolean volatilePersistence;

    public DefaultJpaInstanceConfiguration() {
        //not sure why this is necessary but assigning properties doesn't work without it
        put("javax.persistence.provider", PersistenceProvider.class);
        additionalBindings = new ArrayList<>();
    }

    @Override
    public DefaultJpaInstanceConfiguration provider(Class<?> providerClazz) {
        put("javax.persistence.provider", providerClazz);
        return this;
    }

    @Override
    public boolean providesOptionDao() {
        return provideOptionDao;
    }

    @Override
    public boolean providesPatternDao() {
        return providePatternDao;
    }

    @Override
    public List<BindingPair<?>> getAdditionalBindings() {
        return additionalBindings;
    }

    @Override
    public DefaultJpaInstanceConfiguration db(JpaDb db) {
        this.db = db;
        put(PersistenceUnitProperties.JDBC_DRIVER, db.getDriver());
        put(PersistenceUnitProperties.TARGET_DATABASE, db.getTargetDatabase());
        if (connectionUrl != null) {
            url(connectionUrl);
        }
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration url(String url) {
        this.connectionUrl = url;
        String prefix = (db == null) ? "" : db.getUrlPrefix();
        put(PersistenceUnitProperties.JDBC_URL, prefix + url + ";create=" + autoCreate);
        return this;
    }


    @Override
    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public String getUser() {
        return (String) get(PersistenceUnitProperties.JDBC_USER);
    }

    @Override
    public String getPassword() {
        return (String) get(PersistenceUnitProperties.JDBC_PASSWORD);
    }

    @Override
    public boolean isAutoCreate() {
        return autoCreate;
    }


    public DefaultJpaInstanceConfiguration user(String user) {
        put(PersistenceUnitProperties.JDBC_USER, user);
        return this;
    }

    public DefaultJpaInstanceConfiguration password(String password) {
        put(PersistenceUnitProperties.JDBC_PASSWORD, password);
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration autoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration ddlGeneration(Ddl ddl) {
        put(PersistenceUnitProperties.DDL_GENERATION, ddl.getAction());
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration transactionType(TransactionType transactionType) {
        put(PersistenceUnitProperties.TRANSACTION_TYPE, transactionType.name());
        return this;
    }

    public String getTransactionType() {
        return (String) get(PersistenceUnitProperties.TRANSACTION_TYPE);
    }

    /**
     * Also sets property "eclipselink.logging.level.sql", see http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging
     *
     * @param level
     *
     * @return this
     */
    @Override
    public DefaultJpaInstanceConfiguration loggingLevel(LoggingLevel level) {
        put(PersistenceUnitProperties.LOGGING_LEVEL, level);
        put("eclipselink.logging.level.sql", level);
        return this;
    }

    @Override
    public Properties toProperties() {
        Properties props = new Properties();
        props.putAll(this);
        return props;
    }

    @Override
    public LoggingLevel getLoggingLevel() {
        return (LoggingLevel) get(PersistenceUnitProperties.LOGGING_LEVEL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultJpaInstanceConfiguration useLongIntDao() {
        bind(JpaDao_LongInt.class, DefaultJpaDao_LongInt.class);
        return this;
    }

    /**
     * Adds a interface-implementation pair which will be bound in the PersistenceUnitModule, and attached to an annotation if one has been specified
     *
     * @return this
     */
    @Override
    public <T> DefaultJpaInstanceConfiguration bind(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        BindingPair<T> bindingPair = new BindingPair<>(interfaceClass, implementationClass);
        additionalBindings.add(bindingPair);
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultJpaInstanceConfiguration provideOptionDao() {
        bind(OptionDao.class, DefaultJpaOptionDao.class);
        provideOptionDao = true;
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultJpaInstanceConfiguration providePatternDao() {
        bind(PatternDao.class, DefaultJpaPatternDao.class);
        providePatternDao = true;
        return this;
    }

    @Override
    public I18NKey getName() {
        return name;
    }

    @Override
    public I18NKey getDescription() {
        return description;
    }


    @Override
    public boolean isVolatilePersistence() {
        return volatilePersistence;
    }

    @Override
    public DefaultJpaInstanceConfiguration name(final I18NKey name) {
        this.name = name;
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration description(final I18NKey description) {
        this.description = description;
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration connectionUrl(final String connectionUrl) {
        this.connectionUrl = connectionUrl;
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration volatilePersistence(final boolean volatilePersistence) {
        this.volatilePersistence = volatilePersistence;
        return this;
    }
}
