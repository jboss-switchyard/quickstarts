/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Test for {@link CamelActivator} that uses a implementation.camel and
 * test error handling.
 * 
 * @author Daniel Bevenius
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-activator-impl-error.xml", mixins = CDIMixIn.class)
public class CamelImplementationErrorHandlingTest {

    @ServiceOperation("OrderService.getTitleForItem")
    private Invoker _getTitleForItem;

    @Rule
    public SwitchYardExpectedException thrown = SwitchYardExpectedException.none();

    private CamelContext _camelContext;

    @Before
    public void setupMockEndpoint() {
        final MockEndpoint endpoint = _camelContext.getEndpoint("mock://throw", MockEndpoint.class);
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

}
