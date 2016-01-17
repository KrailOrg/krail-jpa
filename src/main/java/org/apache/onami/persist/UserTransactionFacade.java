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

package org.apache.onami.persist;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.inject.Singleton;
import javax.transaction.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Facade to the {@link javax.transaction.UserTransaction} which wraps all checked exception into runtime exceptions.
 * Adds some convenience methods.
 */
@Singleton
class UserTransactionFacade {

    /**
     * Transaction states in which only a rollback is possible
     */
    private static final Set<Integer> ROLLBACK_ONLY_STATES = new HashSet<Integer>(Arrays.asList(Status.STATUS_MARKED_ROLLBACK, Status.STATUS_ROLLING_BACK,
            Status.STATUS_ROLLEDBACK));

    /**
     * The wrapped user transaction.
     */
    private final UserTransaction txn;

    /**
     * Constructor.
     *
     * @param txn
     *         the actual user transaction to wrap. Must not be {@code null}.
     */
    UserTransactionFacade(UserTransaction txn) {
        this.txn = checkNotNull(txn, "txn is mandatory!");
    }

    /**
     * @see {@link javax.transaction.UserTransaction#begin()}.
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    void begin() {
        try {
            txn.begin();
        } catch (NotSupportedException e) {
            throw new RuntimeException("nested transactions are not supported by the user transaction " + txn, e);
        } catch (SystemException e) {
            throw new RuntimeException("unexpected error occurred", e);
        }
    }

    /**
     * @see {@link javax.transaction.UserTransaction#commit()}.
     */
    @SuppressFBWarnings({"EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS",
            "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
    void commit() {
        try {
            txn.commit();
        } catch (SecurityException e) {
            throw new RuntimeException("not allowed to commit the transaction", e);
        } catch (IllegalStateException e) {
            throw new RuntimeException("no transaction associated with userTransaction", e);
        } catch (RollbackException e) {
            throw new RuntimeException("rollback during commit", e);
        } catch (HeuristicMixedException e) {
            throw new RuntimeException("heuristic partial rollback during commit", e);
        } catch (HeuristicRollbackException e) {
            throw new RuntimeException("heuristic rollback during commit", e);
        } catch (SystemException e) {
            throw new RuntimeException("unexpected error occurred", e);
        }
    }

    /**
     * @see {@link javax.transaction.UserTransaction#rollback()}.
     */
    @SuppressFBWarnings({"EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
    void rollback() {
        try {
            txn.rollback();
        } catch (IllegalStateException e) {
            throw new RuntimeException("no transaction associated with userTransaction", e);
        } catch (SecurityException e) {
            throw new RuntimeException("not allowed to rollback the transaction", e);
        } catch (SystemException e) {
            throw new RuntimeException("unexpected error occurred", e);
        }
    }

    /**
     * @see {@link javax.transaction.UserTransaction#setRollbackOnly()}.
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    void setRollbackOnly() {
        try {
            txn.setRollbackOnly();
        } catch (IllegalStateException e) {
            throw new RuntimeException("no transaction associated with userTransaction", e);
        } catch (SystemException e) {
            throw new RuntimeException("unexpected error occurred", e);
        }
    }

    /**
     * @return {@code true} if this transaction may onl roll back. {@code false} otherwise.
     */
    boolean getRollbackOnly() {
        return ROLLBACK_ONLY_STATES.contains(getStatus());
    }

    /**
     * Retries several times when the status is {@link Status#STATUS_UNKNOWN}.
     * Will abort retrying after approximately one second.
     *
     * @see {@link javax.transaction.UserTransaction#getStatus()}.
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    private int getStatus() {
        try {
            int status = txn.getStatus();
            for (int i = 0; status == Status.STATUS_UNKNOWN && i < 8; i++) {
                try {
                    Thread.sleep((30L * i) + 30L);
                } catch (InterruptedException e) {
                    // do nothing
                }
                status = txn.getStatus();
            }
            return status;
        } catch (SystemException e) {
            throw new RuntimeException("unexpected error occurred", e);
        }
    }

    /**
     * @return {@code true} if there is already a transaction active. {@code false} otherwise.
     */
    boolean isActive() {
        return getStatus() != Status.STATUS_NO_TRANSACTION;
    }

}
