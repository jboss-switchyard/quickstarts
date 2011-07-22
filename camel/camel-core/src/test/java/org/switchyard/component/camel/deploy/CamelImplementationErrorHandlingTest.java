/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.deploy;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.camel.deploy.support.CustomException;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/**
 * Test for {@link CamelActivator} that uses a implementation.camel and
 * test error handling.
 * 
 * @author Daniel Bevenius
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-activator-impl-error.xml", mixins = CDIMixIn.class)
public class CamelImplementationErrorHandlingTest {

    @ServiceOperation("OrderService.getTitleForItem")
    private Invoker _getTitleForItem;

    private Deployment _deployment;

    @Rule
    public SwitchYardExpectedException thrown = SwitchYardExpectedException.none();

    @Before
    public void setupMockEndpoint() {
        final CamelContext camelContext = getCamelContext();
        final MockEndpoint endpoint = camelContext.getEndpoint("mock://throw", MockEndpoint.class);
        endpoint.whenAnyExchangeReceived(new ExceptionThrowingProcesor());
    }

    @Test 
    public void shouldThrowRuntimeExceptionFromCamelRoute() throws Exception {
        thrown.expect(CustomException.class);
        thrown.expectMessage("dummy exception");
        _getTitleForItem.sendInOut("10");
    }
    
    private class ExceptionThrowingProcesor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            throw new CustomException("dummy exception");
        }
    }

    private CamelContext getCamelContext() {
        final CamelActivator activator = (CamelActivator) _deployment.findActivator("camel");
        return activator.getCamelContext();
    }
}
