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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.onami.persist.BindingPair;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultJpaInstanceConfigurationTest {

    DefaultJpaInstanceConfiguration config;

    @Before
    public void setup() {
        config = new DefaultJpaInstanceConfiguration();
    }

    @Test
    public void db() {
        //given

        //when
        config.db(JpaDb.DERBY_EMBEDDED);
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_DRIVER)).isEqualTo("org.apache.derby.jdbc.EmbeddedDriver");
        assertThat(config.get(PersistenceUnitProperties.TARGET_DATABASE)).isEqualTo("Derby");
    }

    @Test
    public void url() {
        //given

        //when
        config.autoCreate(true);
        config.url("jdbc:derby:/home/david/temp/derbyDb");
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" + "create=true");
        assertThat(config.isAutoCreate()).isTrue();
        //when
        config.autoCreate(false);
        config.url("jdbc:derby:/home/david/temp/derbyDb");
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" + "create=false");
        assertThat(config.isAutoCreate()).isFalse();
    }

    @Test
    public void ddl_gen() {
        //given

        //when
        config.ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.CREATE_ONLY);
        //then
        assertThat(config.get(PersistenceUnitProperties.DDL_GENERATION)).isEqualTo("create-tables");

        //when
        config.ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);
        //then
        assertThat(config.get(PersistenceUnitProperties.DDL_GENERATION)).isEqualTo("drop-and-create-tables");
    }

    @Test
    public void urlAfterDb() {
        //given

        //when
        config.db(JpaDb.DERBY_EMBEDDED);
        config.autoCreate(true);
        config.url("/home/david/temp/derbyDb");
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" +
                "create=true");
        assertThat(config.getConnectionUrl()).isEqualTo("/home/david/temp/derbyDb");
    }

    @Test
    public void dbAfterUr() {
        //given

        //when
        config.autoCreate(true);
        config.url("/home/david/temp/derbyDb");
        config.db(JpaDb.DERBY_EMBEDDED);
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" +
                "create=true");
    }

    @Test
    public void user() {
        //given

        //when
        config.user("me");
        //then
        assertThat(config.getUser()).isEqualTo("me");
    }

    @Test
    public void password() {
        //given

        //when
        config.password("*****");
        //then
        assertThat(config.getPassword()).isEqualTo("*****");
    }

    @Test
    public void transactionType() {
        //given

        //when
        config.transactionType(JpaInstanceConfiguration.TransactionType.JTA);
        //then
        assertThat(config.getTransactionType()).isEqualTo("JTA");
    }

    @Test
    public void toProperties() {
        //given
        config.user("me")
              .password("****");
        //when

        Properties props = config.toProperties();
        //then
        assertThat(props.size()).isEqualTo(config.size());
    }

    @Test
    public void loggingLevel() {
        //given

        //when
        config.loggingLevel(JpaInstanceConfiguration.LoggingLevel.ALL);
        //then
        assertThat(config.getLoggingLevel()).isEqualTo(JpaInstanceConfiguration.LoggingLevel.ALL);
    }

    @Test
    public void addBinding() {
        //given

        //when
        config.bind(JpaDao_LongInt.class, DefaultJpaDao_LongInt.class);
        //then
        assertThat(config.getAdditionalBindings()).hasSize(1);
        BindingPair<?> entry = config.getAdditionalBindings()
                                     .get(0);
        assertThat(entry.getInterfaceClass()).isEqualTo(JpaDao_LongInt.class);
        assertThat(entry.getImplementationClass()).isEqualTo(DefaultJpaDao_LongInt.class);
    }
}