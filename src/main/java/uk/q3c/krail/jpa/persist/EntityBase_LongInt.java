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

import uk.q3c.krail.core.data.KrailEntity;

import javax.persistence.*;

/**
 * A convenience base class which implements KrailEntity with ID of Long and Version of Integer.  It also assumes automatic generation of the Id being
 * provided by the database.  Of course, not all databases have that facility
 * <p>
 * Created by David Sowerby on 13/04/15.
 */
@MappedSuperclass
public abstract class EntityBase_LongInt implements KrailEntity<Long, Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }
}
