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

package org.apache.onami.persist.test.multipersistenceunits;

import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.test.TestEntity;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleMultiplePuTest extends BaseMultiplePuTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
        beginUnitOfWork();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        endUnitOfWork();
        super.tearDown();
    }

    @Test
    public void storeUnitsInTwoPersistenceUnits() throws Exception {
        // given

        final TestEntity firstEntity = new TestEntity();
        final TestEntity secondEntity = new TestEntity();

        // when
        firstEmp.get()
                .persist(firstEntity);
        secondEmp.get()
                 .persist(secondEntity);

        // then
        assertThat(firstEmp.get()
                           .find(TestEntity.class, firstEntity.getId())).isNotNull();
        assertThat(secondEmp.get()
                            .find(TestEntity.class, secondEntity.getId())).isNotNull();
        assertThat(firstEmp.get()
                           .find(TestEntity.class, secondEntity.getId())).isNull();
        assertThat(secondEmp.get()
                            .find(TestEntity.class, firstEntity.getId())).isNull();
    }

    //    @Test
    //    public void daoBindings() {
    //        //given
    //        final Widget firstEntity = new Widget();
    //        firstEntity.setId(1L);
    //        firstEntity.setName("A");
    //        final Widget secondEntity = new Widget();
    //        secondEntity.setId(1L);
    //        secondEntity.setName("B");
    //        firstEmp.get()
    //                .persist(firstEntity);
    //        secondEmp.get()
    //                 .persist(secondEntity);
    //
    //        Key<JpaDao_LongInt> daoKey1 = Key.get(JpaDao_LongInt.class);
    //
    //        Key<JpaDao_LongInt> daoKey2 = Key.get(JpaDao_LongInt.class);
    //
    //
    //        //when
    //        final JpaDao_LongInt dao1 = injector.getInstance(daoKey1);
    //
    //
    //        final JpaDao_LongInt dao2 = injector.getInstance(daoKey2);
    //
    //        //bindings to annotations
    //
    //
    //        //then
    //        assertThat(dao1).isNotNull();
    //        Optional<Widget> w1 = dao1.findById( Widget.class, firstEntity.getId());
    //        assertThat(w1).isNotNull();
    //        assertThat(w1.get()
    //                     .getId()).isEqualTo(1);
    //        assertThat(w1.get()
    //                     .getName()).isEqualTo("A");
    //
    //
    //
    //
    //        //then
    //        assertThat(dao2).isNotNull();
    //        Optional<Widget> w2 = dao2.findById( Widget.class, secondEntity.getId());
    //        assertThat(w2).isNotNull();
    //        assertThat(w2.get()
    //                     .getId()).isEqualTo(1);
    //        assertThat(w2.get()
    //                     .getName()).isEqualTo("B");
    //
    //
    //
    //
    //    }

    private String empUrl(EntityManagerProvider firstEmp) {
        final EntityManager entityManager = firstEmp.get();
        EntityManagerImpl em = (EntityManagerImpl) entityManager;
        return (String) em.getProperties()
                          .get(PersistenceUnitProperties.JDBC_URL);

    }

}
