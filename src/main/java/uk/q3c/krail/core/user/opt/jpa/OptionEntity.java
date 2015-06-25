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

package uk.q3c.krail.core.user.opt.jpa;

import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.persist.jpa.EntityBase_LongInt;

import javax.annotation.Nonnull;
import javax.persistence.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An entity to hold data for an {@link Option}.  The value is held as a String to enable the use of a single column for it.
 * <p>
 * Created by David Sowerby on 13/04/15.
 */
@Entity
public class OptionEntity extends EntityBase_LongInt {

    private String optionKey;
    private String rankName;
    private String userHierarchyName;
    private String value;

    public OptionEntity() {
    }

    public OptionEntity(@Nonnull OptionCacheKey optionCacheKey, @Nonnull String value) {
        userHierarchyName = optionCacheKey.getHierarchy()
                                          .persistenceName();
        rankName = optionCacheKey.getRequestedRankName();
        optionKey = optionCacheKey.getOptionKey()
                                  .compositeKey();
        this.value = value;

    }

    public String getUserHierarchyName() {
        return userHierarchyName;
    }

    public void setUserHierarchyName(String userHierarchyName) {
        this.userHierarchyName = userHierarchyName;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
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
