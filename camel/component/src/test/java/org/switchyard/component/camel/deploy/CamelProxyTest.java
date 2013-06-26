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