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

package uk.q3c.krail.persist.jpa.common;

import org.apache.onami.persist.BindingPair;
import org.apache.onami.persist.EntityManagerProvider;
import uk.q3c.krail.i18n.persist.I18NPersistenceEnabler;
import uk.q3c.krail.option.persist.OptionPersistenceEnabler;
import uk.q3c.krail.persist.DataSourceInstanceConfiguration;

import java.util.List;
import java.util.Properties;

/**
 * Created by David Sowerby on 04/04/15.
 */
public interface JpaInstanceConfiguration<C> extends DataSourceInstanceConfiguration<C>, I18NPersistenceEnabler<JpaInstanceConfiguration>, OptionPersistenceEnabler<JpaInstanceConfiguration> {
    enum TransactionType {JTA, RESOURCE_LOCAL}


    enum Ddl {
        CREATE_ONLY("create-tables"), DROP_AND_CREATE("drop-and-create-tables");

        private String action;

        Ddl(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    List<BindingPair<?>> getAdditionalBindings();

    boolean providesOptionDao();

    boolean providesPatternDao();

    C provider(Class<?> providerClazz);

    C db(JpaDb db);

    C ddlGeneration(Ddl ddl);

    C transactionType(TransactionType transactionType);

    C loggingLevel(LoggingLevel level);

    Properties toProperties();

    /**
     * Passes an interface-implementation pair which enables binding of implementations via the Onami-common private modules.  If you have a binding which
     * needs to be bound "to" an {@link EntityManagerProvider} (including those bound with annotations) use this method.
     *
     * @param interfaceClass
     *         the interface to be bound
     * @param implementationClass
     *         the implementation to bind it to
     * @param <T>
     *         the interface type
     *
     * @return this for fluency
     */
    <T> C bind(Class<T> interfaceClass, Class<? extends T> implementationClass);

    /**
     * Shorthand way to bind {@link JpaDao_LongInt}
     *
     * @return this for fluency
     */
    C useLongIntDao();


}
