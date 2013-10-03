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

package org.switchyard.component.bean.invoker;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "ReferenceInvokerTest.xml", mixins = CDIMixIn.class)
public class ReferenceInvokerTest {

    public static final String REFERENCE_A = "ReferenceA";
    public static final String REFERENCE_B = "ReferenceB";
    public static final String TEST_IN_PROPERTY = "TestInProperty";
    public static final String TEST_OUT_PROPERTY = "TestOutProperty";
    private SwitchYardTestKit testKit;

    @ServiceOperation("InvokerService")
    private Invoker invokerService;

    @Test
    public void invokeReferenceA() {
        MockHandler handlerA = testKit.registerInOnlyService(REFERENCE_A);
        MockHandler handlerB = testKit.registerInOnlyService(REFERENCE_B);
        invokerService.operation("testA").sendInOnly("testA");
        Assert.assertEquals(1, handlerA.getMessages().size());
        Assert.assertEquals(0, handlerB.getMessages().size());
    }
    
    @Test
    public void invokeReferenceB() {
        MockHandler handlerA = testKit.registerInOnlyService(REFERENCE_A);
        MockHandler handlerB = testKit.registerInOnlyService(REFERENCE_B);
        invokerService.operation("testB").sendInOnly("testB");
        Assert.assertEquals(0, handlerA.getMessages().size());
        Assert.assertEquals(1, handlerB.getMessages().size());
    }
    
    @Test
    public void invokeNonexistentReference() {
        invokerService.operation("testZ").sendInOnly("testZ");
    }
    
    @Test
    public void invokeWithoutOperationName() {
        invokerService.operation("noOperation").sendInOnly("noOperation");
    }
    
    @Test
    public void getContract() {
        invokerService.operation("getContract").sendInOnly("getContract");
    }
    
    @Test
    public void messageTest() {
        testKit.registerInOutService(REFERENCE_A, new BaseHandler() {
            @Override
            public void handleMessage(Exchange exchange) throws HandlerException {
                // Verify that the content was set on the request message by the invoker
                Assert.assertNotNull(exchange.getMessage().getContent());
                Assert.assertEquals("message-test-in", exchange.getMessage().getContent());
                
                // Set out content to be returned through invoker
                Message reply = exchange.createMessage();
                reply.setContent("message-test-out");
                exchange.send(reply);
            }
        });
    }
    
    @Test
    public void invokeWithContent() {
        testKit.registerInOutService(REFERENCE_A, new BaseHandler() {
            @Override
            public void handleMessage(Exchange exchange) throws HandlerException {
                // Verify that the content was set on the request message by the invoker
                Assert.assertNotNull(exchange.getMessage().getContent());
                Assert.assertEquals("content-test-in", exchange.getMessage().getContent());
            }
        });
    }
    
    @Test
    public void setProperty() {
        testKit.registerInOutService(REFERENCE_A, new BaseHandler() {
            @Override
            public void handleMessage(Exchange exchange) throws HandlerException {
                // Verify that a property was set on the request message by the invoker
                Property inProp = exchange.getContext(exchange.getMessage())
                        .getProperty(TEST_IN_PROPERTY, Scope.MESSAGE);
                Assert.assertNotNull(inProp);
                Assert.assertEquals(TEST_IN_PROPERTY, inProp.getValue());
                
                // Set an out property which will be verified in the test bean class
                Message reply = exchange.createMessage();
                exchange.getContext(reply)
                    .setProperty(TEST_OUT_PROPERTY, TEST_OUT_PROPERTY, Scope.MESSAGE);
                exchange.send(reply);
            }
        });
        invokerService.operation("propertyTest").sendInOnly("propertyTest");
    }
}
