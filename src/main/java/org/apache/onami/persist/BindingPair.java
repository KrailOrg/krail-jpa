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

import uk.q3c.krail.persist.jpa.common.JpaInstanceConfiguration;

import java.io.Serializable;

/**
 * An interface-implementation pair which enables binding of implementations via the Onami-common private modules.  If you have a binding which needs to be
 * bound "to" an {@link EntityManagerProvider} (including those bound with annotations) use {@link JpaInstanceConfiguration#bind(Class, Class)}
 * Created by David Sowerby on 11/04/15.
 */
public class BindingPair<E extends Object> implements Serializable {
    private final Class<? extends E> implementationClass;
    private final Class<E> interfaceClass;


    public BindingPair(Class<E> interfaceClass, Class<? extends E> implementationClass) {
        this.implementationClass = implementationClass;
        this.interfaceClass = interfaceClass;
    }


    public Class<? extends E> getImplementationClass() {
        return implementationClass;
    }

    public Class<E> getInterfaceClass() {
        return interfaceClass;
    }
}

