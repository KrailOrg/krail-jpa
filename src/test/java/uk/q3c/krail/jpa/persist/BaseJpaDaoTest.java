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

package uk.q3c.krail.jpa.persist;

import com.google.inject.Key;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseJpaDaoTest extends JpaDaoTestBase {


    JpaDao_LongInt dao;
    private long count = 1;

    @Override
    public void setUp() {
        super.setUp();
        final Key<JpaDao_LongInt> daoKey = Key.get(JpaDao_LongInt.class, Jpa1.class);
        dao = injector.getInstance(daoKey);
        count = 1;
    }

    @Test
    public void writeRead() {
        //given

        //when
        final Widget widget = dao.save(newWidget("a", "write"));
        //then
        assertThat(widget.getId()).isNotNull();
        final Optional<Widget> byId = dao.findById(Widget.class, 1L);
        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get()
                       .getName()).isEqualTo("a");
    }

    private Widget newWidget(String a, String b) {
        Widget w = new Widget(a, b);
        w.setId(count++);
        return w;
    }

    private Widget2 newWidget2(String a, String b) {
        Widget2 w = new Widget2(a, b);
        w.setId(count++);
        return w;
    }

    @Test
    public void countFindAndFindAll() {
        //given

        //when
        dao.save(newWidget("a", "count"));
        dao.save(newWidget("b", "count"));
        dao.save(newWidget("c", "count"));
        //then
        assertThat(dao.findById(Widget.class, 1L)
                      .isPresent()).isTrue();
        assertThat(dao.findById(Widget.class, 2L)
                      .isPresent()).isTrue();
        assertThat(dao.findById(Widget.class, 3L)
                      .isPresent()).isTrue();
        assertThat(dao.findById(Widget.class, 4L)
                      .isPresent()).isFalse();
        assertThat(dao.findAll(Widget.class)).hasSize(3);
        assertThat(dao.count(Widget.class)).isEqualTo(3);
    }

    @Test
    public void tableName() {
        //given
        //when

        //then
        assertThat(dao.tableName(Widget.class)).isEqualTo("Widget");
    }

    @Test
    public void tableNameFromAnnotation() {
        //given

        //when

        //then
        assertThat(dao.tableName(Widget2.class)).isEqualTo("wiggly");
    }

    @Test
    public void url() {
        //given

        //when

        //then
        assertThat(dao.connectionUrl()).contains("jdbc:derby:/tmp/junit");
    }
}