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

package uk.q3c.krail.persist.jpa.option;

import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.persist.jpa.i18n.KeyValueEntity;

import javax.annotation.Nonnull;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Version;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An entity to hold data for an {@link Option}.  The value is held as a String to enable the use of a single column for it.  Context is part of the overall
 * {#optionKey} to ensure uniqueness, but is also held separately as it is a useful way to group the options for manual setting up, and there may also be an
 * opportunity to pre-load options by Context.
 * <p>
 * Created by David Sowerby on 13/04/15.
 */
@Entity
public class JpaOptionEntity implements KeyValueEntity<JpaOptionId, Integer> {

    @EmbeddedId
    private JpaOptionId id;
    private String value;
    @Version
    private Integer version;

    protected JpaOptionEntity() {
    }

    public JpaOptionEntity(@Nonnull OptionCacheKey optionCacheKey, @Nonnull String value) {
        this.id = new JpaOptionId(optionCacheKey);
        this.value = value;

    }

    @Override
    public JpaOptionId getId() {
        return id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public String getContext() {
        return id.getContext();
    }

    public String getUserHierarchyName() {
        return id.getUserHierarchyName();
    }

    public String getRankName() {
        return id.getRankName();
    }

    public String getOptionKey() {
        return id.getOptionKey();
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    public void setValue(@Nonnull String value) {
        checkNotNull(value);
        this.value = value;
    }


}
