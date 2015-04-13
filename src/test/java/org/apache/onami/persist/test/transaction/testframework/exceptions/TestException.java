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

package org.apache.onami.persist.test.transaction.testframework.exceptions;

/**
 * Exception which can be thrown by a {@link org.apache.onami.persist.test.transaction.testframework.TransactionalTask}.
 */
public class TestException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String message;

    /**
     * Constructor.
     */
    public TestException() {
        message = TestException.class.getSimpleName();
    }

    /**
     * Constructor.
     *
     * @param message
     *         the message of the exception.
     */
    public TestException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
