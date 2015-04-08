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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.onami.persist.EntityManagerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class BaseJpaSpecificDaoTest {

    DefaultStandardTestEntityJpaSpecificDao specificDao;

    @Mock
    EntityManagerProvider entityManagerProvider;

    @Mock
    StandardJpaDao dao;

    @Mock
    StandardTestEntity entity;

    @Before
    public void setup() {
        specificDao = new DefaultStandardTestEntityJpaSpecificDao(entityManagerProvider, dao);
    }

    @Test
    public void save() {
        //given

        //when
        specificDao.save(entity);
        //then
        verify(dao).save(any(), any());
    }

    @Test
    public void findAll() {
        //given

        //when
        specificDao.findAll();
        //then
        verify(dao).findAll(any(), any());
    }

    @Test
    public void findById() {
        //given

        //when
        specificDao.findById(5L);
        //then
        verify(dao).findById(any(), any(), any());
    }


    @Test
    public void deleteById() {
        //given

        //when
        specificDao.deleteById(5L);
        //then
        verify(dao).deleteById(any(), any(), any());
    }


    @Test
    public void load() {
        //given

        //when
        specificDao.load(5L);
        //then
        verify(dao).findById(any(), any(), any());
    }

    @Test
    public void tableName() {
        //given

        //when
        specificDao.tableName();
        //then
        verify(dao).tableName(any(), any());
    }

    @Test
    public void newEntity() {
        //given

        //when
        StandardTestEntity newEntity = specificDao.newEntity();
        //then
        assertThat(newEntity).isNotNull();
    }

}