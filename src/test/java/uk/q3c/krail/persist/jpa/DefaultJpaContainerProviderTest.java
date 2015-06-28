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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.addon.jpacontainer.JPAContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.data.DataModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestJpaModule.class, DataModule.class})
public class DefaultJpaContainerProviderTest {

    @Inject
    Injector injector;

    DefaultJpaContainerProvider provider;

    @Test
    public void get() {
        //given
        provider = new DefaultJpaContainerProvider(injector);
        //when
        JPAContainer<StandardTestEntity> result = provider.get(Jpa1.class, StandardTestEntity.class, JpaContainerProvider.ContainerType.CACHED);
        //then
        assertThat(result).isNotNull();
    }
}