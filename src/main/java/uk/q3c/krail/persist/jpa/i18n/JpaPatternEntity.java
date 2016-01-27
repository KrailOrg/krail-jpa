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

package uk.q3c.krail.persist.jpa.i18n;

import uk.q3c.krail.core.i18n.PatternCacheKey;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Version;

/**
 * An entity to represent an I18N key, Locale and value combination
 * <p>
 * Created by David Sowerby on 15/04/15.
 */

@Entity
public class JpaPatternEntity implements KeyValueEntity<PatternId, Integer> {

    @EmbeddedId
    private PatternId id;
    private String value;
    @Version
    private Integer version;

    protected JpaPatternEntity() {
    }

    public JpaPatternEntity(PatternCacheKey cacheKey, String value) {
        id = new PatternId(cacheKey);
        this.value = value;
    }

    public String getI18nkey() {
        return id.getI18nkey();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLocale() {
        return id.getLocale();
    }

    @Override
    public PatternId getId() {
        return id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }
}
