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

package uk.q3c.krail.core.user.opt.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultJpaOptionDaoTest extends BasePersistenceTest {

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
    public void name() {
        //given

        //when

        //then
        assertThat(true).isFalse();
    }
}
