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

import com.google.inject.Key;
import org.apache.onami.persist.test.multipersistenceunits.BaseMultiplePuTest;
import org.apache.onami.persist.test.multipersistenceunits.FirstPU;
import org.apache.onami.persist.test.multipersistenceunits.SecondPU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DaoTest extends BaseMultiplePuTest {


    Key<StandardJpaDao> daoKey1 = Key.get(StandardJpaDao.class);
    Key<StandardJpaStatementDao> statementDaoKey1 = Key.get(StandardJpaStatementDao.class, FirstPU.class);
    Key<StandardJpaBlockDao> blockDaoKey1 = Key.get(StandardJpaBlockDao.class, FirstPU.class);

    Key<StandardJpaDao> daoKey2 = Key.get(StandardJpaDao.class);
    Key<StandardJpaStatementDao> statementDaoKey2 = Key.get(StandardJpaStatementDao.class, SecondPU.class);
    Key<StandardJpaBlockDao> blockDaoKey2 = Key.get(StandardJpaBlockDao.class, SecondPU.class);

    StandardJpaDao dao1;
    StandardJpaStatementDao statementDao1;
    StandardJpaBlockDao blockDao1;


    StandardJpaDao dao2;
    StandardJpaStatementDao statementDao2;
    StandardJpaBlockDao blockDao2;


    @Override
    @Before
    public void setUp() {
        super.setUp();
        dao1 = injector.getInstance(daoKey1);
        statementDao1 = injector.getInstance(statementDaoKey1);
        blockDao1 = injector.getInstance(blockDaoKey1);


        dao2 = injector.getInstance(daoKey2);
        statementDao2 = injector.getInstance(statementDaoKey2);
        blockDao2 = injector.getInstance(blockDaoKey2);

        beginUnitOfWork();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        endUnitOfWork();
        super.tearDown();
    }


    @Test
    public void saveAndDelete() {
        //given

        blockDao1.transact(dao -> assertThat(dao.connectionUrl()).isEqualTo(statementDao1.connectionUrl()));

        //when save
        saveEntitiesBlock(3);
        saveEntitiesStatement(2);
        //then findAll, count
        List<StandardTestEntity> allEntities = statementDao1.findAll(StandardTestEntity.class);
        assertThat(allEntities).hasSize(5);
        assertThat(statementDao1.count(StandardTestEntity.class)).isEqualTo(5);

        blockDao1.transact(d -> {
                    assertThat(d.findAll(StandardTestEntity.class)).hasSize(5);
                    assertThat(d.count(StandardTestEntity.class)).isEqualTo(5);
                }

        );

        final Long id0 = allEntities.get(0)
                                    .getId();
        final Long id1 = allEntities.get(1)
                                    .getId();

        //when findById
        //then
        assertThat(statementDao1.findById(StandardTestEntity.class, id0)
                                .isPresent()).isTrue();
        assertThat(statementDao1.load(StandardTestEntity.class, id0)
                                .isPresent()).isTrue();

        blockDao1.transact(d -> {

            assertThat(d.findById(StandardTestEntity.class, id0)
                        .isPresent()).isTrue();
            assertThat(d.load(StandardTestEntity.class, id0)
                        .isPresent()).isTrue();
        });

        //when deleteById
        statementDao1.deleteById(StandardTestEntity.class, id0);
        assertThat(statementDao1.count(StandardTestEntity.class)).isEqualTo(4);
        blockDao1.transact(d -> {
            d.deleteById(StandardTestEntity.class, id1);
            assertThat(d.count(StandardTestEntity.class)).isEqualTo(3);
        });

        //when delete(entity)
        final List<StandardTestEntity> allEntities1 = statementDao1.findAll(StandardTestEntity.class);
        statementDao1.delete(allEntities1.get(0));
        assertThat(statementDao1.count(StandardTestEntity.class)).isEqualTo(2);
        blockDao1.transact(d -> {
            d.delete(allEntities1.get(1));
            assertThat(d.count(StandardTestEntity.class)).isEqualTo(1);
        });

        final StandardTestEntity et = allEntities1.get(2);
        final Long id = et.getId();
        final Integer version = et.getVersion();
        assertThat(statementDao1.getIdentity(et)).isEqualTo(id);
        assertThat(statementDao1.getVersion(et)).isEqualTo(version);

        //whewn getId & version
        blockDao1.transact(d -> {
            assertThat(d.getIdentity(et)).isEqualTo(id);
            assertThat(d.getVersion(et)).isEqualTo(version);
        });

        //table name
        final String tableName = StandardTestEntity.class.getSimpleName();
        assertThat(statementDao1.tableName(StandardTestEntity.class)).isEqualTo(tableName);
        blockDao1.transact(d -> {
            assertThat(d.tableName(StandardTestEntity.class)).isEqualTo(tableName);
        });


        //merge
        StandardTestEntity et1 = et;
        et1.setFirstName("changed");
        final StandardTestEntity merged = statementDao1.merge(et1);
        assertThat(merged.getId()).isEqualTo(et.getId());

        blockDao1.transact(d -> {
            Optional<StandardTestEntity> et2opt = statementDao1.findById(StandardTestEntity.class, et.getId());
            assertThat(et2opt.isPresent());
            StandardTestEntity et2 = et2opt.get();
            StandardTestEntity merged2 = (StandardTestEntity) d.merge(et2);
            assertThat(merged2.getId()).isEqualTo(et.getId());
        });


    }

    private void saveEntitiesBlock(final int count) {
        blockDao1.transact(d -> {
            for (int i = 0; i < count; i++) {
                final StandardTestEntity entity = newEntity("block" + i);
                d.save(entity);
            }
        });
    }

    private StandardTestEntity newEntity(String firstName) {
        StandardTestEntity entity = new StandardTestEntity();
        entity.setFirstName(firstName);
        return entity;
    }

    private void saveEntitiesStatement(int count) {
        for (int i = 0; i < count; i++) {
            final StandardTestEntity entity = newEntity("statement" + i);
            statementDao1.save(entity);
        }
    }
}
