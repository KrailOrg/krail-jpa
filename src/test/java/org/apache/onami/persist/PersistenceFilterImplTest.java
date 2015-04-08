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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static org.mockito.Mockito.*;

/**
 * Test for {@link PersistenceFilterImpl}.
 */
public class PersistenceFilterImplTest {

    private AllPersistenceServices allPersistenceServices;
    private AllUnitsOfWork allUnitsOfWork;
    private PersistenceFilterImpl sut;

    @Before
    public void setUp() throws Exception {
        allPersistenceServices = mock(AllPersistenceServices.class);
        allUnitsOfWork = mock(AllUnitsOfWork.class);
        sut = new PersistenceFilterImpl(allPersistenceServices, allUnitsOfWork);
    }

    @Test
    public void initShouldStartService() throws Exception {
        sut.init(mock(FilterConfig.class));
        verify(allPersistenceServices).startAllStoppedPersistenceServices();
    }

    @Test
    public void destroyShouldStopService() {
        sut.destroy();
        verify(allPersistenceServices).stopAllPersistenceServices();
    }

    @Test
    public void doFilterShouldSpanUnitOfWork() throws Exception {
        // given
        final FilterChain chain = mock(FilterChain.class);
        final InOrder inOrder = inOrder(allUnitsOfWork, chain);

        final ServletRequest request = mock(ServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);

        // when
        sut.doFilter(request, response, chain);

        // then
        inOrder.verify(allUnitsOfWork)
               .beginAllInactiveUnitsOfWork();
        inOrder.verify(chain)
               .doFilter(request, response);
        inOrder.verify(allUnitsOfWork)
               .endAllUnitsOfWork();
    }

    @Test(expected = RuntimeException.class)
    public void doFilterShouldEndUnitOfWorkInCaseOfException() throws Exception {
        // given
        final FilterChain chain = mock(FilterChain.class);
        final InOrder inOrder = inOrder(allUnitsOfWork, chain);

        final ServletRequest request = mock(ServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);

        doThrow(new RuntimeException()).when(chain)
                                       .doFilter(request, response);

        // when
        try {
            sut.doFilter(request, response, chain);
        }
        // then
        finally {
            inOrder.verify(allUnitsOfWork)
                   .beginAllInactiveUnitsOfWork();
            inOrder.verify(chain)
                   .doFilter(request, response);
            inOrder.verify(allUnitsOfWork)
                   .endAllUnitsOfWork();
        }
    }

}
