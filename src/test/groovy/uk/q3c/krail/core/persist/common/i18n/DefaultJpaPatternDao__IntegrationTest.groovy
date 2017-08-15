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

package uk.q3c.krail.core.persist.common.i18n

import com.google.inject.Inject
import org.apache.onami.persist.PersistenceService
import org.apache.onami.persist.UnitOfWork
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.core.vaadin.DataModule
import uk.q3c.krail.i18n.persist.PatternCacheKey
import uk.q3c.krail.i18n.persist.PatternDao
import uk.q3c.krail.i18n.test.TestLabelKey
import uk.q3c.krail.persist.jpa.common.Jpa1
import uk.q3c.krail.persist.jpa.i18n.JpaPatternEntity
import uk.q3c.krail.persist.jpa.i18n.TestPatternJpaModule
import  uk.q3c.krail.i18n.persist.PatternCacheKey

@UseModules([TestPatternJpaModule, DataModule])
class DefaultJpaPatternDao__IntegrationTest extends Specification {

    @Inject
    @Jpa1
    PersistenceService persistenceService;

    @Inject
    @Jpa1
    PatternDao dao;


    @Inject
    @Jpa1
    UnitOfWork unitOfWork;


    def setup() {
        persistenceService.start();
        unitOfWork.begin();
    }

    def cleanup() {
        unitOfWork.end();
        persistenceService.stop();
    }


    def "setting up"() {
        expect:
        persistenceService != null
        persistenceService.isRunning()

        unitOfWork != null
        unitOfWork.isActive()

        dao != null


    }


    def "write a single value"() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.UK);
        when:
        dao.write(cacheKey, "4");
        JpaPatternEntity actual = dao.find(cacheKey)
        then:
        actual != null
        dao.count() == 1
        actual.getValue() == "4"
    }


    def "write with the same cache key multiple times and ensure there is only the latest value present in persistence"() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.UK);
        when:
        dao.write(cacheKey, "4");
        dao.write(cacheKey, "5");
        dao.write(cacheKey, "6");
        JpaPatternEntity actual = dao.find(cacheKey)

        then:
        actual != null
        dao.count() == 1
        actual.getValue() == "6"

    }

    def "write multiple keys and delete one"() {
        given:
        PatternCacheKey cacheKey1 = new PatternCacheKey(TestLabelKey.Yes, Locale.UK);
        PatternCacheKey cacheKey2 = new PatternCacheKey(TestLabelKey.Yes, new Locale("EN"));
        PatternCacheKey cacheKey3 = new PatternCacheKey(TestLabelKey.Yes, Locale.US);

        when:

        dao.write(cacheKey1, "3")
        dao.write(cacheKey2, "4")
        dao.write(cacheKey3, "5")

        then:
        dao.count() == 3

        when:

        dao.deleteValue(cacheKey1)
        JpaPatternEntity result2 = dao.find(cacheKey2)
        JpaPatternEntity result3 = dao.find(cacheKey3)

        then:

        dao.count() == 2
        result2.getValue() == "4"
        result3.getValue() == "5"


    }


}