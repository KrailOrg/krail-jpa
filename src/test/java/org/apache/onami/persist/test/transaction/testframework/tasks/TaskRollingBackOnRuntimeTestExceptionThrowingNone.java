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

package org.apache.onami.persist.test.transaction.testframework.tasks;

import org.apache.onami.persist.Transactional;
import org.apache.onami.persist.test.TestEntity;
import org.apache.onami.persist.test.transaction.testframework.TransactionalTask;
import org.apache.onami.persist.test.transaction.testframework.exceptions.RuntimeTestException;
import org.apache.onami.persist.test.transaction.testframework.exceptions.TestException;

/**
 * Task which stores an entity and will:
 * - roll back on {@link RuntimeTestException}.
 * - throw no new Exception
 */
public class TaskRollingBackOnRuntimeTestExceptionThrowingNone extends TransactionalTask {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackOn = RuntimeTestException.class)
    public void doTransactional() throws TestException, RuntimeTestException {
        storeEntity(new TestEntity());
        doOtherTasks();
    }

}
