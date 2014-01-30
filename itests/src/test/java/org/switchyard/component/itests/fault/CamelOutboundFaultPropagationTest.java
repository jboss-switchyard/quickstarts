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
package org.switchyard.component.itests.fault;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;

/**
 * Functional test for a fault propagation from camel outbound binding.
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = "switchyard-camel-outbound-fault-test.xml",
        mixins = { CDIMixIn.class, TransactionMixIn.class, HornetQMixIn.class })
public class CamelOutboundFaultPropagationTest  {
    @ServiceOperation("GreetingService.greet")
    private Invoker _greet;
    @ServiceOperation("StoreService.store")
    private Invoker _store;
    
    @Test
    public void testReferenceBindingInOut() throws Exception {
        final String payload = "store/IN_OUT";
        Message msg = null;
        try {
            msg = _store.sendInOut(payload);
        } catch (Throwable t) {
            System.out.println("Received an Exception: " + formatThrowable(t));
            return;
        }
        Assert.fail("It succeeded while an Exception is expected: " + msg.getContent());
    }
    
    @Test
    public void testReferenceBindingInOnly() throws Exception {
        final String payload = "store/IN_ONLY";
        try {
            _store.sendInOnly(payload);
        } catch (Throwable t) {
            System.out.println("Received an Exception: " + formatThrowable(t));
            return;
        }
        Assert.fail("It succeeded while an Exception is expected");
    }
    
    @Test
    public void testBeanServiceInOut() throws Exception {
        final String payload = "greet/IN_OUT";
        Message msg = null;
        try {
            msg = _greet.sendInOut(payload);
        } catch (Throwable t) {
            System.out.println("Received an Exception: " + formatThrowable(t));
            return;
        }
        Assert.fail("It succeeded while an Exception is expected: " + msg.getContent());
    }
    
    @Test
    public void testBeanServiceInOnly() throws Exception {
        final String payload = "greet/IN_ONLY";
        try {
            _greet.sendInOnly(payload);
        } catch (Throwable t) {
            System.out.println("Received an Exception: " + formatThrowable(t));
            return;
        }
        Assert.fail("It succeeded while an Exception is expected");
    }
    
    private String formatThrowable(Throwable t) {
        StringBuilder buf = new StringBuilder(t.getClass().getName())
                .append(" : ")
                .append(t.getMessage());
        Throwable cause = t;
        while((cause = cause.getCause()) != null) {
            buf.append(" -- ")
               .append(cause.getClass().getName())
               .append(" : ")
               .append(cause.getMessage());
        }
        return buf.toString();
    }
}

