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

import javax.inject.Inject;
import javax.servlet.*;
import java.io.IOException;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Implementation of {@link PersistenceFilter}.
 */
class PersistenceFilterImpl implements PersistenceFilter {

    /**
     * Container of all known persistence services.
     */
    private final AllPersistenceServices allPersistenceServices;

    /**
     * Container of all known units of work.
     */
    private final AllUnitsOfWork allUnitsOfWork;

    /**
     * Constructor.
     *
     * @param allPersistenceServices
     *         container of all known persistence services.
     * @param allUnitsOfWork
     *         container of all known units of work.
     */
    @Inject
    PersistenceFilterImpl(AllPersistenceServices allPersistenceServices, AllUnitsOfWork allUnitsOfWork) {
        this.allPersistenceServices = checkNotNull(allPersistenceServices, "allPersistenceServices is mandatory!");
        this.allUnitsOfWork = checkNotNull(allUnitsOfWork, "allUnitsOfWork is mandatory!");
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            allUnitsOfWork.beginAllInactiveUnitsOfWork();
            chain.doFilter(request, response);
        } finally {
            allUnitsOfWork.endAllUnitsOfWork();
        }
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        allPersistenceServices.startAllStoppedPersistenceServices();
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void destroy() {
        allPersistenceServices.stopAllPersistenceServices();
    }

}
