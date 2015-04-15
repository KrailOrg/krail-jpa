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

package uk.q3c.krail.i18n.jpa;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.jpa.BaseJpaTest;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.PatternCacheKey;
import uk.q3c.krail.persist.jpa.Jpa1;
import uk.q3c.krail.persist.jpa.TestJpaModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestJpaModule.class})
public class BaseJpaPatternDaoTest extends BaseJpaTest {


    public static final String UK_ENGLISH = "UK English";
    public static final String ENGLISH = "English";
    public static final String US_ENGLISH = "US English";

    @Inject
    @Jpa1
    JpaPatternDao dao;

    PatternCacheKey cacheKey1 = new PatternCacheKey(LabelKey.Yes, Locale.UK);
    PatternCacheKey cacheKey2 = new PatternCacheKey(LabelKey.Yes, new Locale("EN"));
    PatternCacheKey cacheKey3 = new PatternCacheKey(LabelKey.Yes, Locale.US);

    @Test
    public void allMethods() {
        //given

        //when
        dao.write(cacheKey1, UK_ENGLISH);
        dao.write(cacheKey2, ENGLISH);
        dao.write(cacheKey3, US_ENGLISH);
        //then
        assertThat(dao.getValue(cacheKey1)
                      .get()).isEqualTo(UK_ENGLISH);
        assertThat(dao.getValue(cacheKey2)
                      .get()).isEqualTo(ENGLISH);
        assertThat(dao.getValue(cacheKey3)
                      .get()).isEqualTo(US_ENGLISH);

        //when
        String returnedValue = dao.deleteValue(cacheKey2);

        //then
        assertThat(dao.getValue(cacheKey1)
                      .get()).isEqualTo(UK_ENGLISH);
        assertThat(dao.getValue(cacheKey2)
                      .isPresent()).isFalse();
        assertThat(dao.getValue(cacheKey3)
                      .get()).isEqualTo(US_ENGLISH);
        assertThat(returnedValue).isEqualTo(ENGLISH);

        //when absent
        returnedValue = dao.deleteValue(cacheKey2);

        //then
        assertThat(returnedValue).isNull();

        //when
        String url = dao.connectionUrl();

        //then
        assertThat(url).startsWith("jdbc:derby:/tmp/junit");
    }
}