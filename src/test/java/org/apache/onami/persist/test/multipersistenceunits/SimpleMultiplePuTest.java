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

import org.apache.onami.persist.test.TestEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
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
    public void storeUnitsInTwoPersistenceUnits() {
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


}
