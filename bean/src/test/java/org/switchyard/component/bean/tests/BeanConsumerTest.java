/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.bean.tests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.component.bean.BeanComponentException;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/*
 * Assorted methods for testing a CDI bean consuming a service in SwitchYard.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class BeanConsumerTest {

    @ServiceOperation("ConsumerService.consumeInOnlyService")
    private Invoker inOnly;
    @ServiceOperation("ConsumerService.consumeInOutService")
    private Invoker inOut;
    @ServiceOperation("ConsumerService.unknownXOp")
    private Invoker unknownXOp;

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
    public void consumeInOnlyServiceFromBean_Fault_invalid_opertion() {
        try {
            // this should result in a fault
            unknownXOp.sendInOut("hello");
            // if we got here, then our negative test failed
            Assert.fail("Invalid operation allowed!");
        } catch (InvocationFaultException infEx) {
            Message faultMsg = infEx.getFaultMessage();
            BeanComponentException e = faultMsg.getContent(BeanComponentException.class);
            Assert.assertEquals("Operation name 'unknownXOp' must resolve to exactly one bean method on bean type '" + 
                    ConsumerService.class.getName() + "'.", e.getMessage());
        }
    }

    @Test
    public void consumeInOnlyServiceFromBean_Fault_service_exception() {
        try {
            // this should result in a fault
            inOut.sendInOut(new ConsumerException("throw me a remote exception please!!"));
            // if we got here, then our negative test failed
            Assert.fail("Exception thrown by bean but not turned into fault!");
        } catch (InvocationFaultException infEx) {
            Message faultMsg = infEx.getFaultMessage();
            Assert.assertTrue(faultMsg.getContent() instanceof BeanComponentException);
            BeanComponentException beanEx = faultMsg.getContent(BeanComponentException.class);
            Assert.assertEquals("Invocation of operation 'consumeInOutService' on bean component '" + 
                    ConsumerBean.class.getName() + "' failed with exception.  See attached cause.", beanEx.getMessage());
            Assert.assertTrue(infEx.isType(ConsumerException.class));
            Assert.assertEquals("remote-exception-received", beanEx.getCause().getCause().getMessage());
        }
    }
}
