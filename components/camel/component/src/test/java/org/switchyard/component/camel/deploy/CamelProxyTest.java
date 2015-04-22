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

import static org.junit.Assert.assertNotNull;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Test header presence populated by CamelMessageComposer in camel messages.
 * 
 * @author Lukasz Dywicki
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-activator-proxy-test.xml", mixins = CDIMixIn.class)
public class CamelProxyTest {

    private final static String MATH_SERVICE = "CamelMathService";

    private SwitchYardCamelContext _camelContext;

    @ServiceOperation(MATH_SERVICE)
    private Invoker invoker;

    @Before
    public void verifyContext() {
        assertNotNull(_camelContext.getServiceDomain());
    }

    @Test
    public void shouldInvokeDifferentOperations() throws Exception {
        MockEndpoint all = _camelContext.getEndpoint("mock:all", MockEndpoint.class);
        
        all.expectedBodiesReceived(100.0);
        all.expectedPropertyReceived(Exchange.OPERATION_NAME, "pow");
        invoker.operation("pow").sendInOut(100.0);
        all.assertIsSatisfied();
        
        all.reset();
        
        all.expectedBodiesReceived(101.0);
        all.expectedPropertyReceived(Exchange.OPERATION_NAME, "cos");
        invoker.operation("cos").sendInOut(101.0);
        all.assertIsSatisfied();

        all.reset();
        
        all.expectedBodiesReceived(11.0);
        all.expectedPropertyReceived(Exchange.OPERATION_NAME, "pow");
        invoker.operation("pow").sendInOut(11.0);
        all.assertIsSatisfied();
    }

}