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
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;

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
    

    private SwitchYardTestKit _testKit;

    @ServiceOperation(MATH_SERVICE)
    private Invoker invoker;

    @Test
    public void shouldInvokeDifferentOperations() throws Exception {
        final CamelContext camelContext = getCamelContext();

        MockEndpoint unknown = camelContext.getEndpoint("mock:unknown", MockEndpoint.class);
        unknown.expectedMessageCount(0);

        MockEndpoint all = camelContext.getEndpoint("mock:all", MockEndpoint.class);
        all.expectedMessageCount(2);
        all.expectedBodiesReceived(100.0, 101.0);
        // in this case 
        all.expectedHeaderReceived(Exchange.SERVICE_NAME, MATH_ALL_SERVICE_NS);
        all.expectedHeaderReceived(Exchange.SERVICE_NAME, MATH_ALL_SERVICE_NS);

        MockEndpoint cos = camelContext.getEndpoint("mock:cos", MockEndpoint.class);
        cos.expectedMessageCount(1);
        cos.expectedBodiesReceived(101.0);
        cos.expectedHeaderReceived(Exchange.SERVICE_NAME, MATH_COS_SERVICE_NS);

        MockEndpoint abs = camelContext.getEndpoint("mock:abs", MockEndpoint.class);
        abs.expectedMessageCount(1);
        abs.expectedBodiesReceived(100.0);
        abs.expectedHeaderReceived(Exchange.SERVICE_NAME, MATH_ABS_SERVICE_NS);

        invoker.operation("abs").sendInOut(100.0);
        invoker.operation("cos").sendInOut(101.0);

        unknown.assertIsSatisfied();
        all.assertIsSatisfied();
        abs.assertIsSatisfied();
        cos.assertIsSatisfied();
    }

    @Test
    public void shouldReahUnknown() throws Exception {
        final CamelContext camelContext = getCamelContext();

        MockEndpoint unknown = camelContext.getEndpoint("mock:unknown", MockEndpoint.class);
        unknown.expectedBodiesReceived(10.1);
        unknown.expectedHeaderReceived(Exchange.OPERATION_NAME, "pow");
        unknown.expectedHeaderReceived(Exchange.SERVICE_NAME, MATH_SERVICE_NS);
        unknown.expectedHeaderReceived(Exchange.FAULT_TYPE, "java:" + IllegalArgumentException.class.getName());

        invoker.operation("pow").sendInOut(10.1);

        unknown.assertIsSatisfied();
    }

    private CamelActivator getCamelActivator() {
        final Deployment deployment = (Deployment) _testKit.getDeployment();
        return (CamelActivator) deployment.findActivator("camel");
    }

    private CamelContext getCamelContext() {
        final CamelActivator activator = getCamelActivator();
        return activator.getCamelContext();
    }

}
