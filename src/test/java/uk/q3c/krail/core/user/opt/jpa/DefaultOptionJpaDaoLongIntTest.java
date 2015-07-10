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

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.persist.jpa.Jpa1;
import uk.q3c.krail.persist.jpa.JpaDaoTestBase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.q3c.krail.core.user.profile.RankOption.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionJpaDaoLongIntTest extends JpaDaoTestBase {


    OptionJpaDao_LongInt dao;

    @Mock
    UserHierarchy hierarchy1;

    ImmutableList<String> rankNames1 = ImmutableList.of("fred", "accounts", "finance");
    private OptionCacheKey cacheKey0;
    private OptionCacheKey cacheKey1;
    private OptionCacheKey cacheKey2;
    private OptionKey<Integer> optionKey1;

    @Before
    public void setup() {
        super.setUp();
        final Key<OptionJpaDao_LongInt> daoKey = Key.get(OptionJpaDao_LongInt.class, Jpa1.class);
        dao = injector.getInstance(daoKey);
        when(hierarchy1.persistenceName()).thenReturn("p1");
        when(hierarchy1.ranksForCurrentUser()).thenReturn(rankNames1);
        when(hierarchy1.rankName(0)).thenReturn(rankNames1.get(0));
        when(hierarchy1.rankName(1)).thenReturn(rankNames1.get(1));
        when(hierarchy1.rankName(2)).thenReturn(rankNames1.get(2));
        optionKey1 = new OptionKey<>(288, LocaleContainer.class, LabelKey.Yes);
        cacheKey0 = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, 0, optionKey1);
        cacheKey1 = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, 1, optionKey1);
        cacheKey2 = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, 2, optionKey1);


    }

    @Test
    public void writeRead() {
        //given


        //when
        dao.write(cacheKey0, Optional.of(73));
        Optional<?> actual = dao.getValue(cacheKey0);
        //then

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(73);
        System.out.println(dao.connectionUrl());
    }



    @Test
    public void getValuesUnconverted() {
        //given
        dao.write(cacheKey0, Optional.of(73));
        dao.write(cacheKey1, Optional.of(44));
        dao.write(cacheKey2, Optional.of(195));
        //when
        Optional<?> highestRankedValue = dao.getHighestRankedValue(new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<?> lowestRankedValue = dao.getLowestRankedValue(new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<?> specificRankedValue0 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        Optional<?> specificRankedValue1 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(1), SPECIFIC_RANK));
        Optional<?> specificRankedValue2 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(2), SPECIFIC_RANK));
        //then
        assertThat(highestRankedValue.get()
                                     .toString()).isEqualTo("73");
        assertThat(lowestRankedValue.get()
                                    .toString()).isEqualTo("195");
        assertThat(specificRankedValue0.get()
                                       .toString()).isEqualTo("73");
        assertThat(specificRankedValue1.get()
                                       .toString()).isEqualTo("44");
        assertThat(specificRankedValue2.get()
                                       .toString()).isEqualTo("195");
    }

    @Test
    public void getValuesConverted() {
        //given
        dao.write(cacheKey0, Optional.of(73));
        dao.write(cacheKey1, Optional.of(44));
        dao.write(cacheKey2, Optional.of(195));
        //when
        Optional<?> highestRankedValue = dao.getHighestRankedValue(new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<?> lowestRankedValue = dao.getLowestRankedValue(new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<?> specificRankedValue0 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        Optional<?> specificRankedValue1 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(1), SPECIFIC_RANK));
        Optional<?> specificRankedValue2 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(2), SPECIFIC_RANK));
        Optional<?> highestRankedValue1 = dao.getHighestRankedValue(new OptionCacheKey(cacheKey0, "rubbish", HIGHEST_RANK));
        //then
        assertThat(highestRankedValue.get()).isEqualTo(73);
        assertThat(lowestRankedValue.get()).isEqualTo(195);
        assertThat(specificRankedValue0.get()).isEqualTo(73);
        assertThat(specificRankedValue1.get()).isEqualTo(44);
        assertThat(specificRankedValue2.get()).isEqualTo(195);
        assertThat(highestRankedValue1.get()).isEqualTo(73);
    }

    @Test
    public void delete() {
        //given
        dao.write(cacheKey0, Optional.of(73));
        dao.write(cacheKey1, Optional.of(44));
        assertThat(dao.count()).isEqualTo(2);
        //when
        Optional<?> deletedValue = dao.deleteValue(cacheKey1);
        //then
        assertThat(dao.getValue(cacheKey0)
                      .isPresent()).isTrue();
        assertThat(dao.getValue(cacheKey1)
                      .isPresent()).isFalse();
        assertThat(deletedValue).isEqualTo(Optional.of("44"));
        assertThat(dao.count()).isEqualTo(1);

        //when deleted when not there
        deletedValue = dao.deleteValue(cacheKey1);

        //then
        assertThat(dao.getValue(cacheKey0)
                      .isPresent()).isTrue();
        assertThat(dao.getValue(cacheKey1)
                      .isPresent()).isFalse();
        assertThat(deletedValue.isPresent()).isFalse();
    }

    @Test
    public void noValues_converted() {
        //given

        //when
        Optional<?> highestRankedValue = dao.getHighestRankedValue(new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<?> lowestRankedValue = dao.getLowestRankedValue(new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<?> specificRankedValue0 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        //then
        assertThat(highestRankedValue.isPresent()).isFalse();
        assertThat(lowestRankedValue.isPresent()).isFalse();
        assertThat(specificRankedValue0.isPresent()).isFalse();
    }


    @Test
    public void noValues_unconverted() {
        //given

        //when
        Optional<?> highestRankedValue = dao.getHighestRankedValue(new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<?> lowestRankedValue = dao.getLowestRankedValue(new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<?> specificRankedValue0 = dao.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        //then
        assertThat(highestRankedValue.isPresent()).isFalse();
        assertThat(lowestRankedValue.isPresent()).isFalse();
        assertThat(specificRankedValue0.isPresent()).isFalse();
    }

    @Test
    public void clear() {
        //given
        dao.write(cacheKey0, Optional.of(73));
        dao.write(cacheKey1, Optional.of(44));
        assertThat(dao.getValue(cacheKey0)
                      .isPresent()).isTrue();
        assertThat(dao.getValue(cacheKey1)
                      .isPresent()).isTrue();
        //when
        dao.clear();

        //then
        assertThat(dao.getValue(cacheKey0)
                      .isPresent()).isFalse();
        assertThat(dao.getValue(cacheKey1)
                      .isPresent()).isFalse();
        assertThat(dao.count()).isEqualTo(0);
    }
}