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

package uk.q3c.krail.persist.jpa.common;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import org.junit.Test;
import uk.q3c.krail.persist.ContainerType;
import uk.q3c.krail.persist.VaadinContainerProvider;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;


public class DefaultJpaContainerProviderTest extends JpaDaoTestBase {


    @Test
    public void get() {
        //given
        VaadinContainerProvider provider1 = getInstance(VaadinContainerProvider.class, Jpa1.class);
        VaadinContainerProvider provider2 = getInstance(VaadinContainerProvider.class, Jpa2.class);
        //when
        Container container1 = provider1.get(StandardTestEntity.class, ContainerType.CACHED);
        Container container2 = provider2.get(StandardTestEntity.class, ContainerType.CACHED);
        //then

        assertThat(container1).isNotNull();
        assertThat(container2).isNotNull();
        assertThat(targetDb(container1)).isEqualTo("Derby");
        assertThat(targetDb(container2)).isEqualTo("HSQL");

    }

    private String targetDb(Container container) {
        JPAContainer jpaContainer = (JPAContainer) container;
        Map<String, Object> props = jpaContainer.getEntityProvider()
                                                .getEntityManager()
                                                .getProperties();
        return (String) props.get("eclipselink.target-database");
    }
}