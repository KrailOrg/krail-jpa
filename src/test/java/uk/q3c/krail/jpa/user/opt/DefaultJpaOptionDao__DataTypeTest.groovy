/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.jpa.user.opt

import com.google.common.collect.Lists
import com.google.inject.Inject
import org.apache.commons.collections15.ListUtils
import org.apache.onami.persist.PersistenceService
import org.apache.onami.persist.UnitOfWork
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.core.data.DataModule
import uk.q3c.krail.core.user.opt.AnnotationOptionList
import uk.q3c.krail.core.user.opt.OptionDao
import uk.q3c.krail.core.user.opt.OptionKey
import uk.q3c.krail.core.user.opt.OptionList
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey
import uk.q3c.krail.core.user.profile.RankOption
import uk.q3c.krail.core.user.profile.UserHierarchy
import uk.q3c.krail.core.view.component.LocaleContainer
import uk.q3c.krail.i18n.ClassPatternSource
import uk.q3c.krail.i18n.LabelKey
import uk.q3c.krail.jpa.persist.Jpa1
import uk.q3c.krail.jpa.persist.Jpa2

import static uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType

/**
 * Round-trip tests for different data types used by Option
 */
@UseModules([TestOptionJpaModule, DataModule])
class DefaultJpaOptionDao__DataTypeTest extends Specification {


    @Inject
    @Jpa1
    PersistenceService persistenceService;

    @Inject
    @Jpa1
    OptionDao dao;


    @Inject
    @Jpa1
    UnitOfWork unitOfWork;


    def userHierarchy = Mock(UserHierarchy);

    OptionKey<String> optionKeyString = new OptionKey<>("a", LocaleContainer.class, LabelKey.Yes, "a");
    OptionKey<Integer> optionKeyInteger = new OptionKey<>(21, LocaleContainer.class, LabelKey.Yes, "b");
    OptionKey<Boolean> optionKeyBoolean = new OptionKey<>(true, LocaleContainer.class, LabelKey.Yes, "c");
    OptionKey<Long> optionKeyLong = new OptionKey<>(121L, LocaleContainer.class, LabelKey.Yes, "d");
    OptionKey<BigDecimal> optionKeyBigDecimal = new OptionKey<>(new BigDecimal(23.3), LocaleContainer.class, LabelKey.Yes, "e");
    OptionKey<SortType> optionKeyEnum = new OptionKey<>(SortType.INSERTION, LocaleContainer.class, LabelKey.Yes, "f");
    OptionKey<Locale> optionKeyLocale = new OptionKey<>(Locale.CHINA, LocaleContainer.class, LabelKey.Yes, "g");

    OptionKey<OptionList<String>> optionKeyStringImmutableSet = new OptionKey<>(new OptionList<String>(String.class), LocaleContainer.class, LabelKey.Yes, "h");
    OptionKey<AnnotationOptionList> optionKeyAnnotationList = new OptionKey<>(new AnnotationOptionList(Jpa2.class, Jpa1.class), LocaleContainer.class, LabelKey.Yes, "i")

    def setup() {
        userHierarchy.persistenceName() >> "simple"
        userHierarchy.rankName(0) >> "ds"
        userHierarchy.rankName(1) >> "ds"
        persistenceService.start();
        unitOfWork.begin();
    }

    def cleanup() {
        unitOfWork.end();
        persistenceService.stop();
    }


    def "round trip, single value data types"() {
        given:
        OptionCacheKey cacheKeyString = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyString);
        OptionCacheKey cacheKeyInteger = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyInteger);
        OptionCacheKey cacheKeyBoolean = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyBoolean);
        OptionCacheKey cacheKeyLong = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyLong);
        OptionCacheKey cacheKeyBigDecimal = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyBigDecimal);
        OptionCacheKey cacheKeyEnum = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyEnum);
        OptionCacheKey cacheKeyLocale = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyLocale);
        when:
        dao.write(cacheKeyString, Optional.of("4"));
        dao.write(cacheKeyInteger, Optional.of(41));
        dao.write(cacheKeyBoolean, Optional.of(false));
        dao.write(cacheKeyLong, Optional.of(200L));
        dao.write(cacheKeyBigDecimal, Optional.of(new BigDecimal(341.44)));
        dao.write(cacheKeyEnum, Optional.of(SortType.POSITION));
        dao.write(cacheKeyLocale, Optional.of(Locale.CANADA_FRENCH));


        then:
        dao.getValue(cacheKeyString).get().equals("4")
        dao.getValue(cacheKeyInteger).get().equals(41)
        dao.getValue(cacheKeyBoolean).get().equals(false)
        dao.getValue(cacheKeyLong).get().equals(200L)
        dao.getValue(cacheKeyBigDecimal).get().equals(new BigDecimal(341.44))
        dao.getValue(cacheKeyEnum).get().equals(SortType.POSITION)
        dao.getValue(cacheKeyLocale).get().equals(Locale.CANADA_FRENCH)
    }

    def "round trip OptionList"() {

        given:
        OptionList<String> optionList = new OptionList<>(Lists.newArrayList("a", "b"), String.class)
        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyStringImmutableSet);

        when:
        dao.write(cacheKey, Optional.of(optionList));
        OptionList<String> result = dao.getValue(cacheKey).get();
        then:
        ListUtils.isEqualList(result.getList(), optionList.getList());

    }


    def "round trip OptionList with comma in entry"() {

        given:
        OptionList<String> optionList = new OptionList<>(Lists.newArrayList("a,c", "b"), String.class)
        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyStringImmutableSet);

        when:
        dao.write(cacheKey, Optional.of(optionList));
        OptionList<String> result = dao.getValue(cacheKey).get();
        then:
        ListUtils.isEqualList(result.getList(), optionList.getList());

    }

    def "round trip AnnotationOptionList"() {
        given:
        AnnotationOptionList optionList = new AnnotationOptionList(ClassPatternSource, Jpa1)
        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKeyAnnotationList);

        when:
        dao.write(cacheKey, Optional.of(optionList));
        AnnotationOptionList result = dao.getValue(cacheKey).get();
        then:
        ListUtils.isEqualList(result.getList(), optionList.getList());
    }
//    def "core Option data types"() {
//

//        LinkedHashSet
//        ImmutableSwet
//        List
//        Set
//        ArrayList
//
//
//
//        StringPersistenceConverter
//
//
//    }


}