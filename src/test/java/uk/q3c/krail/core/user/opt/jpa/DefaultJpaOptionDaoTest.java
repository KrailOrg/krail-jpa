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
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
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
import uk.q3c.krail.persist.jpa.TestJpaModule;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.q3c.krail.core.user.profile.RankOption.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestJpaModule.class})
public class DefaultJpaOptionDaoTest extends BaseJpaTest {


    @Inject
    @Jpa1
    JpaOptionDao dao1;

    @Mock
    UserHierarchy hierarchy1;

    ImmutableList<String> rankNames1 = ImmutableList.of("fred", "accounts", "finance");
    private OptionCacheKey cacheKey0;
    private OptionCacheKey cacheKey1;
    private OptionCacheKey cacheKey2;
    private OptionKey<Integer> optionKey1;

    @Before
    public void setup() {
        when(hierarchy1.persistenceName()).thenReturn("p1");
        when(hierarchy1.ranksForCurrentUser()).thenReturn(rankNames1);
        when(hierarchy1.rankName(0)).thenReturn(rankNames1.get(0));
        when(hierarchy1.rankName(1)).thenReturn(rankNames1.get(1));
        when(hierarchy1.rankName(2)).thenReturn(rankNames1.get(2));
        optionKey1 = new OptionKey<>(337, LocaleContainer.class, LabelKey.Yes);
        cacheKey0 = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, 0, optionKey1);
        cacheKey1 = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, 1, optionKey1);
        cacheKey2 = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, 2, optionKey1);
    }

    @Test
    public void writeRead() {
        //given


        //when
        dao1.write(cacheKey0, Ints.stringConverter()
                                  .reverse(), 73);
        Optional<Integer> actual = dao1.getValue(Ints.stringConverter(), cacheKey0);
        //then

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(73);
        System.out.println(dao1.connectionUrl());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void writeNotSupported() {
        //given

        //when
        dao1.write(cacheKey0, 1);
        //then
        assertThat(true).isFalse();
    }

    @Test
    public void getValuesUnconverted() {
        //given
        dao1.write(cacheKey0, Ints.stringConverter()
                                  .reverse(), 73);
        dao1.write(cacheKey1, Ints.stringConverter()
                                  .reverse(), 44);
        dao1.write(cacheKey2, Ints.stringConverter()
                                  .reverse(), 195);
        //when
        Optional<Object> highestRankedValue = dao1.getHighestRankedValue(new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<Object> lowestRankedValue = dao1.getLowestRankedValue(new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<Object> specificRankedValue0 = dao1.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        Optional<Object> specificRankedValue1 = dao1.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(1), SPECIFIC_RANK));
        Optional<Object> specificRankedValue2 = dao1.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(2), SPECIFIC_RANK));
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
        dao1.write(cacheKey0, Ints.stringConverter()
                                  .reverse(), 73);
        dao1.write(cacheKey1, Ints.stringConverter()
                                  .reverse(), 44);
        dao1.write(cacheKey2, Ints.stringConverter()
                                  .reverse(), 195);
        //when
        Optional<Integer> highestRankedValue = dao1.getHighestRankedValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<Integer> lowestRankedValue = dao1.getLowestRankedValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<Integer> specificRankedValue0 = dao1.getValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        Optional<Integer> specificRankedValue1 = dao1.getValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, rankNames1.get(1), SPECIFIC_RANK));
        Optional<Integer> specificRankedValue2 = dao1.getValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, rankNames1.get(2), SPECIFIC_RANK));
        Optional<Integer> highestRankedValue1 = dao1.getHighestRankedValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, "rubbish", HIGHEST_RANK));
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
        dao1.write(cacheKey0, Ints.stringConverter()
                                  .reverse(), 73);
        dao1.write(cacheKey1, Ints.stringConverter()
                                  .reverse(), 44);
        //when
        Object deletedValue = dao1.deleteValue(cacheKey1);
        //then
        assertThat(dao1.getValue(cacheKey0)
                       .isPresent()).isTrue();
        assertThat(dao1.getValue(cacheKey1)
                       .isPresent()).isFalse();
        assertThat(deletedValue.toString()).isEqualTo("44");

        //when deleted when not there
        deletedValue = dao1.deleteValue(cacheKey1);

        //then
        assertThat(dao1.getValue(cacheKey0)
                       .isPresent()).isTrue();
        assertThat(dao1.getValue(cacheKey1)
                       .isPresent()).isFalse();
        assertThat(deletedValue).isNull();
    }

    @Test
    public void noValues_converted() {
        //given

        //when
        Optional<Integer> highestRankedValue = dao1.getHighestRankedValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<Integer> lowestRankedValue = dao1.getLowestRankedValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<Integer> specificRankedValue0 = dao1.getValue(Ints.stringConverter(), new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        //then
        assertThat(highestRankedValue.isPresent()).isFalse();
        assertThat(lowestRankedValue.isPresent()).isFalse();
        assertThat(specificRankedValue0.isPresent()).isFalse();
    }


    @Test
    public void noValues_unconverted() {
        //given

        //when
        Optional<Object> highestRankedValue = dao1.getHighestRankedValue(new OptionCacheKey(cacheKey0, HIGHEST_RANK));
        Optional<Object> lowestRankedValue = dao1.getLowestRankedValue(new OptionCacheKey(cacheKey0, LOWEST_RANK));
        Optional<Object> specificRankedValue0 = dao1.getValue(new OptionCacheKey(cacheKey0, rankNames1.get(0), SPECIFIC_RANK));
        //then
        assertThat(highestRankedValue.isPresent()).isFalse();
        assertThat(lowestRankedValue.isPresent()).isFalse();
        assertThat(specificRankedValue0.isPresent()).isFalse();
    }
}
