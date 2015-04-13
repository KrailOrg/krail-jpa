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

package org.apache.onami.persist;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Provider fro {@link UserTransaction} which retrieves the value from the JNDI context.
 */
@Singleton
class UserTransactionProviderByJndiLookup implements Provider<UserTransaction> {

    private final String jndiName;

    private final JndiLookupHelper jndiLookupHelper;

    /**
     * Constructor.
     *
     * @param jndiName
     *         jndi name of the entity manager factory. Must not be {@code null}.
     */
    UserTransactionProviderByJndiLookup(@UserTransactionJndiName String jndiName, JndiLookupHelper jndiLookupHelper) {
        this.jndiName = checkNotNull(jndiName, "jndiName is mandatory!");
        this.jndiLookupHelper = checkNotNull(jndiLookupHelper, "jndiLookupHelper is mandatory!");
    }

    /**
     * Gets a {@link UserTransaction} by looking it up in the JNDI context.
     *
     * @return the found entity user transaction
     *
     * @throws RuntimeException
     *         when no user transaction was found.
     */
    //@Override
    public UserTransaction get() {
        return jndiLookupHelper.doJndiLookup(UserTransaction.class, jndiName);
    }

}
