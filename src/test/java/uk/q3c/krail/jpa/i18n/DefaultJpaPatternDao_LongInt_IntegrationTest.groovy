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

package uk.q3c.krail.jpa.i18n

import com.google.inject.Inject
import org.apache.onami.persist.PersistenceService
import org.apache.onami.persist.UnitOfWork
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.core.data.DataModule
import uk.q3c.krail.i18n.LabelKey
import uk.q3c.krail.i18n.PatternCacheKey
import uk.q3c.krail.i18n.PatternDao
import uk.q3c.krail.jpa.persist.Jpa1

@UseModules([TestPatternJpaModule, DataModule])
class DefaultJpaPatternDao_LongInt_IntegrationTest extends Specification {

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
        persistenceService.isRunning() == true

        unitOfWork != null
        unitOfWork.isActive() == true

        dao != null


    }


    def "write a single value"() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        when:
        dao.write(cacheKey, "4");
        Optional<PatternEntity_LongInt> actual = (Optional<PatternEntity_LongInt>) dao.find(cacheKey)
        then:
        actual.isPresent()
        dao.count() == 1
        actual.get().getValue() == "4"
    }


    def "write with the same cache key multiple times and ensure there is only the latest value present in persistence"() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        when:
        dao.write(cacheKey, "4");
        dao.write(cacheKey, "5");
        dao.write(cacheKey, "6");
        Optional<PatternEntity_LongInt> actual = (Optional<PatternEntity_LongInt>) dao.find(cacheKey)

        then:
        actual.isPresent()
        dao.count() == 1
        actual.isPresent()
        actual.get().getValue() == "6"

    }

    def "write multiple keys and delete one"() {
        given:
        PatternCacheKey cacheKey1 = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        PatternCacheKey cacheKey2 = new PatternCacheKey(LabelKey.Yes, new Locale("EN"));
        PatternCacheKey cacheKey3 = new PatternCacheKey(LabelKey.Yes, Locale.US);

        when:

        dao.write(cacheKey1, "3")
        dao.write(cacheKey2, "4")
        dao.write(cacheKey3, "5")

        then:
        dao.count() == 3

        when:

        dao.deleteValue(cacheKey1)
        Optional<PatternEntity_LongInt> result2 = dao.find(cacheKey2)
        Optional<PatternEntity_LongInt> result3 = dao.find(cacheKey3)

        then:

        dao.count() == 2
        result2.get().getValue() == "4"
        result3.get().getValue() == "5"


    }


}