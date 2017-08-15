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


import uk.q3c.krail.persist.KrailEntity;

import javax.persistence.*;

@Entity
public class Widget implements KrailEntity<Long,Integer> {
    private String description;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Version
    private Integer version;

    public Widget() {
    }

    public Widget(String name, String description) {
        this.description = description;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String summary) {
        this.name = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }
}
