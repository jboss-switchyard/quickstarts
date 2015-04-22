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
@SwitchYardTestCaseConfig(config = "switchyard-activator-math-test.xml", mixins = CDIMixIn.class)
public class CamelMathTest {

    private final static String SERVICE_NAMESPACE = "{urn:camel-core:test:1.0}";

    private final static String MATH_ALL_SERVICE_NS = SERVICE_NAMESPACE + "MathAll";
    private final static String MATH_COS_SERVICE_NS = SERVICE_NAMESPACE + "MathCos";
    private final static String MATH_ABS_SERVICE_NS = SERVICE_NAMESPACE + "MathAbs";
    private final static String MATH_SERVICE = "CamelMathService";
    private final static String MATH_SERVICE_NS = SERVICE_NAMESPACE + MATH_SERVICE;

    private SwitchYardCamelContext _camelContext;

    @ServiceOperation(MATH_SERVICE)
    private Invoker invoker;

    @Before
    public void verifyContext() {
        assertNotNull(_camelContext.getServiceDomain());
    }

    @Test
    public void shouldInvokeDifferentOperations() throws Exception {
        MockEndpoint unknown = _camelContext.getEndpoint("mock:unknown", MockEndpoint.class);
        unknown.expectedMessageCount(0);

        MockEndpoint all = _camelContext.getEndpoint("mock:all", MockEndpoint.class);
        all.expectedMessageCount(2);
        all.expectedBodiesReceived(100.0, 101.0);
        // in this case 
        all.expectedPropertyReceived(Exchange.SERVICE_NAME, MATH_ALL_SERVICE_NS);
        all.expectedPropertyReceived(Exchange.SERVICE_NAME, MATH_ALL_SERVICE_NS);

        MockEndpoint cos = _camelContext.getEndpoint("mock:cos", MockEndpoint.class);
        cos.expectedMessageCount(1);
        cos.expectedBodiesReceived(101.0);
        cos.expectedPropertyReceived(Exchange.SERVICE_NAME, MATH_COS_SERVICE_NS);

        MockEndpoint abs = _camelContext.getEndpoint("mock:abs", MockEndpoint.class);
        abs.expectedMessageCount(1);
        abs.expectedBodiesReceived(100.0);
        abs.expectedPropertyReceived(Exchange.SERVICE_NAME, MATH_ABS_SERVICE_NS);

        invoker.operation("abs").sendInOut(100.0);
        invoker.operation("cos").sendInOut(101.0);

        unknown.assertIsSatisfied();
        all.assertIsSatisfied();
        abs.assertIsSatisfied();
        cos.assertIsSatisfied();
    }

    @Test
    public void shouldReahUnknown() throws Exception {
        MockEndpoint unknown = _camelContext.getEndpoint("mock:unknown", MockEndpoint.class);
        unknown.expectedBodiesReceived(10.1);
        unknown.expectedPropertyReceived(Exchange.OPERATION_NAME, "pow");
        unknown.expectedPropertyReceived(Exchange.SERVICE_NAME, MATH_SERVICE_NS);
        unknown.expectedPropertyReceived(Exchange.FAULT_TYPE, "java:" + IllegalArgumentException.class.getName());

        invoker.operation("pow").sendInOut(10.1);

        unknown.assertIsSatisfied();
    }

}
