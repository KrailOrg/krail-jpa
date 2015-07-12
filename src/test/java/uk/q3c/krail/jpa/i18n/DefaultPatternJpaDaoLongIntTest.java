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

package uk.q3c.krail.jpa.i18n;

import com.google.inject.Key;
import org.junit.Test;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.PatternCacheKey;
import uk.q3c.krail.jpa.persist.Jpa1;
import uk.q3c.krail.jpa.persist.JpaDaoTestBase;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class DefaultPatternJpaDaoLongIntTest extends JpaDaoTestBase {


    public static final String UK_ENGLISH = "UK English";
    public static final String ENGLISH = "English";
    public static final String US_ENGLISH = "US English";


    PatternCacheKey cacheKey1 = new PatternCacheKey(LabelKey.Yes, Locale.UK);
    PatternCacheKey cacheKey2 = new PatternCacheKey(LabelKey.Yes, new Locale("EN"));
    PatternCacheKey cacheKey3 = new PatternCacheKey(LabelKey.Yes, Locale.US);

    PatternJpaDao dao;
    private long count = 1;

    @Override
    public void setUp() {
        super.setUp();
        final Key<PatternJpaDao> daoKey = Key.get(PatternJpaDao.class, Jpa1.class);
        dao = injector.getInstance(daoKey);
        count = 1;
    }

    @Test
    public void allMethods() {
        //given

        //        when
        dao.write(cacheKey1, UK_ENGLISH);
        dao.write(cacheKey2, ENGLISH);
        dao.write(cacheKey3, US_ENGLISH);
        //then
        assertThat(this.dao.getValue(cacheKey1)
                           .get()).isEqualTo(UK_ENGLISH);
        assertThat(this.dao.getValue(cacheKey2)
                           .get()).isEqualTo(ENGLISH);
        assertThat(this.dao.getValue(cacheKey3)
                           .get()).isEqualTo(US_ENGLISH);

        //when
        Optional<String> returnedValue = this.dao.deleteValue(cacheKey2);

        //then
        assertThat(this.dao.getValue(cacheKey1)
                           .get()).isEqualTo(UK_ENGLISH);
        assertThat(this.dao.getValue(cacheKey2)
                           .isPresent()).isFalse();
        assertThat(this.dao.getValue(cacheKey3)
                           .get()).isEqualTo(US_ENGLISH);
        assertThat(returnedValue.get()).isEqualTo(ENGLISH);

        //when absent
        returnedValue = this.dao.deleteValue(cacheKey2);

        //then
        assertThat(returnedValue.isPresent()).isFalse();

        //when
        String url = this.dao.connectionUrl();

        //then
        assertThat(url).startsWith("jdbc:derby:/tmp/junit");
    }
}