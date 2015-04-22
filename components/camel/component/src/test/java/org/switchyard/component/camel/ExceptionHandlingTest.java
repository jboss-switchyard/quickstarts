/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.camel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.Scope;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-exception-test.xml")
public class ExceptionHandlingTest {

    private SwitchYardTestKit testKit;
    @ServiceOperation("UnhandledExceptionService")
    private Invoker unhandledException;
    @ServiceOperation("OnExceptionService")
    private Invoker onException;
    @ServiceOperation("TryCatchService")
    private Invoker tryCatch;

    @Test
    public void testUnhandledException() {
        final String exMsg = "Service1 Exception";
        
        testKit.registerInOutService("Service1")
            .replyWithFault(new Exception(exMsg));
        
        try {
            unhandledException.operation("process").sendInOut("test");
            Assert.fail("Exception from Service1 not propagated");
        } catch (InvocationFaultException ifEx) {
            Assert.assertEquals(exMsg, ifEx.getCause().getCause().getMessage());
        }
    }
    
    @Test
    public void testOnExceptionFailurePath() {
        final String exMsg = "Service1 Exception";
        
        testKit.registerInOutService("Service1")
            .replyWithFault(new Exception(exMsg));
        
        try {
            String result = onException.operation("process")
                    .sendInOut("test").getContent(String.class);
            Assert.assertEquals("handled:" + exMsg, result);
            
        } catch (Exception ex) {
            Assert.fail("Exception from Service1 not handled");
        }
    }
    
    /**
     * Make sure that happy path is not impacted by updated logic to screen
     * off "Camel*" exchange properties.
     */
    @Test
    public void testOnExceptionHappyPath() {
        testKit.registerInOutService("Service1", new ExchangeHandler() {
            public void handleFault(Exchange exchange) { }
            public void handleMessage(Exchange exchange) throws HandlerException {
                // check to make sure that when Service1 is called, the correct properties were passed
                Assert.assertEquals("Camel-abc", exchange.getContext().getPropertyValue("Camel-abc"));
                Assert.assertNull(exchange.getContext().getProperty("Camel-123"));
                
                // now set two properties, one of which should flow back
                exchange.getContext().setProperty("Camel-xyz", "Camel-xyz", Scope.EXCHANGE);
                exchange.getContext().setProperty("Camel-456", "Camel-456", Scope.EXCHANGE);
            }
        });
        String result = onException.operation("process")
                .sendInOut("test").getContent(String.class);
        Assert.assertEquals("happy path", result);
    }
    
    @Test
    public void testTryCatchFailurePath() {
        final String exMsg = "Service1 Exception";
        
        testKit.registerInOutService("Service1")
            .replyWithFault(new Exception(exMsg));
        
        try {
            String result = tryCatch.operation("process")
                    .sendInOut("test").getContent(String.class);
            Assert.assertEquals("handled:" + exMsg, result);
            
        } catch (Exception ex) {
            Assert.fail("Exception from Service1 not handled");
        }
    }
    
    /**
     * Make sure that happy path is not impacted by updated logic to screen
     * off "Camel*" exchange properties.
     */
    @Test
    public void testTryCatchHappyPath() {
        testKit.registerInOutService("Service1").forwardInToOut();
        String result = tryCatch.operation("process")
                .sendInOut("test").getContent(String.class);
        Assert.assertEquals("happy path", result);
    }
}
