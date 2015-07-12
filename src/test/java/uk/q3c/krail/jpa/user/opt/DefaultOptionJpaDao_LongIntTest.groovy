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

package uk.q3c.krail.jpa.user.opt

import com.vaadin.data.util.converter.ConverterFactory
import com.vaadin.data.util.converter.DefaultConverterFactory
import org.apache.onami.persist.EntityManagerProvider
import spock.lang.Specification
import uk.q3c.krail.core.data.DefaultStringPersistenceConverter
import uk.q3c.krail.core.data.StringPersistenceConverter
import uk.q3c.krail.core.user.opt.OptionContext
import uk.q3c.krail.core.user.opt.OptionKey
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey
import uk.q3c.krail.core.user.opt.cache.OptionKeyException
import uk.q3c.krail.core.user.profile.RankOption
import uk.q3c.krail.core.user.profile.UserHierarchy

import javax.persistence.EntityManager
import javax.persistence.TypedQuery

/**
 *
 * Test for {@link uk.q3c.krail.jpa.user.opt.DefaultOptionJpaDao_LongInt}
 *
 * Created by David Sowerby on 10/07/15.
 */
class DefaultOptionJpaDao_LongIntTest extends Specification {


    ConverterFactory converterFactory = new DefaultConverterFactory()

    StringPersistenceConverter stringPersistenceConverter = new DefaultStringPersistenceConverter(converterFactory)

    EntityManagerProvider entityManagerProvider = Mock()

    EntityManager entityManager = Mock()

    UserHierarchy userHierarchy = Mock()

    TypedQuery<OptionEntity_LongInt> query = Mock()

    List<OptionEntity_LongInt> emptyResultList;

    OptionKey optionKey1 = Mock()

    DefaultOptionJpaDao_LongInt dao


    def setup() {
        optionKey1.compositeKey() >> "a_composite_key"
        optionKey1.getContext() >> OptionContext.class
        entityManagerProvider.get() >> entityManager
        emptyResultList = new ArrayList<>()
        userHierarchy.persistenceName() >> "simple"
        userHierarchy.rankName(_) >> "ds"
        dao = new DefaultOptionJpaDao_LongInt(entityManagerProvider, stringPersistenceConverter)
    }

    def "Write should throw an OptionKeyException if OptionKey rank is not specific"() {

        given:

        userHierarchy.highestRankName() >> "ds"
        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.HIGHEST_RANK, optionKey1)


        when:
        dao.write(cacheKey, Optional.of(3))

        then:
        thrown(OptionKeyException)
    }

    def "Write the first time should create a new OptionEntity and persist it"() {
        given:

        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKey1)

        query.getResultList() >> emptyResultList
        entityManager.createQuery(_, _) >> query


        when:
        Object entity = dao.write(cacheKey, Optional.of(3))

        then:
        1 * entityManager.persist(_)
        entity != null
        entity instanceof OptionEntity_LongInt
        OptionEntity_LongInt rEntity = (OptionEntity_LongInt) entity
        rEntity.getValue() == "3"

    }

    def "Write the second time should change the value of the existing OptionEntity and persist it"() {
        given:
        List<OptionEntity_LongInt> resultList = new ArrayList<>();
        OptionCacheKey cacheKey = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, optionKey1)
        emptyResultList = new ArrayList<>()
        query.getResultList() >> emptyResultList
        query.getResultList() >> resultList
        entityManager.createQuery(_, _) >> query


        when:
        Object entity = dao.write(cacheKey, Optional.of(3))
        resultList.add((OptionEntity_LongInt) entity)
        entity = dao.write(cacheKey, Optional.of(5))

        then:
        2 * entityManager.persist(_)
        entity != null
        entity instanceof OptionEntity_LongInt
        OptionEntity_LongInt rEntity = (OptionEntity_LongInt) entity
        rEntity.getValue() == "5"

    }
}
