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

package uk.q3c.krail.jpa.i18n;

import uk.q3c.krail.core.data.EnumConverter;
import uk.q3c.krail.i18n.PatternCacheKey;

import javax.annotation.Nonnull;
import javax.persistence.Embeddable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by David Sowerby on 17 Jan 2016
 */
@Embeddable
public class PatternId {
    private String i18nkey;
    private String locale;

    protected PatternId() {

    }

    public PatternId(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        final Enum<?> enumKey = cacheKey.getKey();
        this.i18nkey = new EnumConverter().convertToString(enumKey);
        this.locale = cacheKey.getRequestedLocale()
                              .toLanguageTag();
    }

    public String getI18nkey() {
        return i18nkey;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatternId that = (PatternId) o;

        if (!i18nkey.equals(that.i18nkey)) return false;
        return locale.equals(that.locale);

    }

    @Override
    public int hashCode() {
        int result = i18nkey.hashCode();
        return 31 * result + locale.hashCode();
    }
}
