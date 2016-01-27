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

package uk.q3c.krail.jpa.user.opt;

import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;

import javax.annotation.Nonnull;
import javax.persistence.Embeddable;

/**
 * Created by David Sowerby on 17 Jan 2016
 */
@Embeddable
public class OptionId {


    private String context;
    private String optionKey;
    private String rankName;
    private String userHierarchyName;

    protected OptionId() {

    }

    public OptionId(@Nonnull OptionCacheKey optionCacheKey) {
        userHierarchyName = optionCacheKey.getHierarchy()
                                          .persistenceName();
        rankName = optionCacheKey.getRequestedRankName();
        OptionKey<?> optKey = optionCacheKey.getOptionKey();
        optionKey = optKey.compositeKey();
        context = optKey.getContext()
                                .getName();
    }

    public String getContext() {
        return context;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public String getRankName() {
        return rankName;
    }

    public String getUserHierarchyName() {
        return userHierarchyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionId optionId = (OptionId) o;

        if (context != null ? !context.equals(optionId.context) : optionId.context != null) return false;
        if (optionKey != null ? !optionKey.equals(optionId.optionKey) : optionId.optionKey != null) return false;
        if (rankName != null ? !rankName.equals(optionId.rankName) : optionId.rankName != null) return false;
        return userHierarchyName != null ? userHierarchyName.equals(optionId.userHierarchyName) : optionId.userHierarchyName == null;

    }

    @Override
    public int hashCode() {
        int result = context != null ? context.hashCode() : 0;
        result = 31 * result + (optionKey != null ? optionKey.hashCode() : 0);
        result = 31 * result + (rankName != null ? rankName.hashCode() : 0);
        return 31 * result + (userHierarchyName != null ? userHierarchyName.hashCode() : 0);
    }
}
