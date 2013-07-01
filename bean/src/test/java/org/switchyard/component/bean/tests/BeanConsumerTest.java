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

package org.switchyard.component.bean.tests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.bean.BeanComponentException;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/*
 * Assorted methods for testing a CDI bean consuming a service in SwitchYard.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class BeanConsumerTest {

    @ServiceOperation("ConsumerService.consumeInOnlyNoArgsService")
    private Invoker inOnlyNoArgs;
    @ServiceOperation("ConsumerService.consumeInOnlyService")
    private Invoker inOnly;
    @ServiceOperation("ConsumerService.consumeInOutService")
    private Invoker inOut;
    @ServiceOperation("ConsumerService.consumeInOutServiceThrowsRuntimeException")
    private Invoker inOutRuntimeEx;
    @ServiceOperation("ConsumerService.unknownXOp")
    private Invoker unknownXOp;

    @Test
    public void consumeInOnlyServiceFromBean_without_args() {
        inOnlyNoArgs.sendInOnly(null);
    }
    
    @Test
    public void consumeInOnlyServiceFromBean_new_way() {
        inOnly.sendInOnly("hello");
    }

    @Test
    public void consumeInOutServiceFromBean_new_way() {
        Message responseMsg = inOut.sendInOut("hello");

        Assert.assertEquals("hello", responseMsg.getContent());
    }

    @Test
    public void consumeInOnlyServiceFromBean_Fault_invalid_operation() {
        try {
            // this should result in a fault
            unknownXOp.sendInOut("hello");
            // if we got here, then our negative test failed
            Assert.fail("Invalid operation allowed!");
        } catch (InvocationFaultException ifEx) {
            Assert.assertEquals(
                    "Operation unknownXOp is not included in interface for service: ConsumerService", 
                    ifEx.getFaultMessage().getContent(Exception.class).getMessage());
        }
    }

    @Test
    public void consumeInOutServiceFromBean_Fault_service_exception() {
        try {
            // this should result in a fault
            inOut.sendInOut(new ConsumerException("throw me a remote exception please!!"));
            // if we got here, then our negative test failed
            Assert.fail("Exception thrown by bean but not turned into fault!");
        } catch (InvocationFaultException infEx) {
            System.out.println(infEx.getFaultMessage().getContent());
            Message faultMsg = infEx.getFaultMessage();
            Assert.assertTrue(faultMsg.getContent() instanceof ConsumerException);
            Assert.assertEquals("remote-exception-received", faultMsg.getContent(ConsumerException.class).getMessage());
        }
    }
    
    @Test
    public void consumeInOutServiceFromBean_throws_runtime_exception() {
        try {
            // this should result in a fault
            inOutRuntimeEx.sendInOut(new ConsumerException("throw me a remote exception please!!"));
            // if we got here, then our negative test failed
            Assert.fail("Exception thrown by bean but not turned into fault!");
        } catch (InvocationFaultException infEx) {
            System.out.println(infEx.getFaultMessage().getContent());
            Message faultMsg = infEx.getFaultMessage();
            Assert.assertTrue(faultMsg.getContent() instanceof HandlerException);
            Assert.assertEquals("throw me a remote exception please!!", faultMsg.getContent(HandlerException.class).getCause().getCause().getMessage());
        }
    }
}
