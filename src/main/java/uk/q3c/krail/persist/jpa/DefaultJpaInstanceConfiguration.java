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

import org.apache.onami.persist.BindingPair;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Helper class to populate the equivalent of persistence.xml properties.  A persistence.xml file is still needed but
 * can be minimal (persistent unit declarations and
 * Created by David Sowerby on 01/01/15.
 */
public class DefaultJpaInstanceConfiguration extends HashMap<String, Object> implements JpaInstanceConfiguration<DefaultJpaInstanceConfiguration> {


    private List<BindingPair<?>> additionalBindings;
    private boolean autoCreate;
    private boolean create;
    private JpaDb db;
    private String url;
    public DefaultJpaInstanceConfiguration() {
        //not sure why this is necessary but assigning properties doesn't work without it
        provider(PersistenceProvider.class);
        additionalBindings = new ArrayList<>();
    }

    @Override
    public DefaultJpaInstanceConfiguration provider(Class<?> providerClazz) {
        put("javax.persistence.provider", providerClazz);
        return this;
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
        if (url != null) {
            url(url);
        }
        return this;
    }

    @Override
    public DefaultJpaInstanceConfiguration url(String url) {
        this.url = url;
        String prefix = (db == null) ? "" : db.getUrlPrefix();
        put(PersistenceUnitProperties.JDBC_URL, prefix + url + ";create=" + autoCreate);
        return this;
    }


    @Override
    public String getUrl() {
        return url;
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

    /**
     * Adds a interface-implementation pair which will be bound in the PersistenceUnitModule, and attached to an annotation if one has been specified
     *
     * @return this
     */
    @Override
    public <T> DefaultJpaInstanceConfiguration addPrivateBinding(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        BindingPair<T> bindingPair = new BindingPair<>(interfaceClass, implementationClass);
        additionalBindings.add(bindingPair);
        return this;
    }

    @Override
    public LoggingLevel getLoggingLevel() {
        return (LoggingLevel) get(PersistenceUnitProperties.LOGGING_LEVEL);
    }
}
