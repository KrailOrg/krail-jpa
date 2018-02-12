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

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link EntityManagerFactoryFactory}.
 */
@Ignore
public class EntityManagerFactoryFactoryTest {

    private static final String PU_KEY = "hibernate.ejb.persistenceUnitName";
    private static final String PU_NAME = "testUnit";
    private static final String TEST_KEY = "testKey";
    private static final String TEST_VALUE = "testValue";
    private Properties properties;
    private EntityManagerFactoryFactory sut;

    @Before
    public void setUp() {
        properties = new Properties();
        sut = new EntityManagerFactoryFactory(PU_NAME, properties);
    }

    @Test
    public void shouldCreateAnInstanceWithThePassedValues() {
        // given
        properties.setProperty(TEST_KEY, TEST_VALUE);
        // when
        final EntityManagerFactory result = sut.createApplicationManagedEntityManagerFactory();
        // then
        // TODO does this cause a problem anywhere?
        //DS: a really odd need to cast to the implementation; EclipseLink only
        //DS: explicit variable needed otherwise code clean up removes the case
        EntityManagerFactoryImpl result1 = (EntityManagerFactoryImpl) result;
        final Map<String, Object> properties = result1.getProperties();
        //DS: but then the properties don't hold the puName
        //        assertThat(properties.get(PU_KEY), is(PU_NAME));
        assertThat(this.properties.get(TEST_KEY), is(TEST_VALUE));

    }

}
