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

import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import javax.persistence.EntityManagerFactory;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link EntityManagerFactorySourceViaProvider}.
 */
public class EntityManagerFactorySourceViaProviderTest {

    private Provider<EntityManagerFactory> emfProvider;
    private EntityManagerFactorySourceViaProvider sut;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        emfProvider = mock(Provider.class);
        sut = new EntityManagerFactorySourceViaProvider(emfProvider);
    }

    @Test
    public void shouldReturnValueFromProvider() {
        // given
        EntityManagerFactory emfDummy = mock(EntityManagerFactory.class);
        doReturn(emfDummy).when(emfProvider)
                          .get();
        // when
        final EntityManagerFactory result = sut.getEntityManagerFactory();
        // then
        assertThat(result, sameInstance(emfDummy));
    }

}
