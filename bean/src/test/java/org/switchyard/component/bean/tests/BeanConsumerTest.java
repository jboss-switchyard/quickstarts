/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.bean.tests;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.InvocationFaultException;
import org.switchyard.Message;
import org.switchyard.SwitchYardCDITestCase;
import org.switchyard.component.bean.BeanComponentException;

/*
 * Assorted methods for testing a CDI bean consuming a service in SwitchYard.
 */
public class BeanConsumerTest extends SwitchYardCDITestCase {

    @Test
    public void consumeInOnlyServiceFromBean_new_way() {
        newInvoker("ConsumerBean.consumeInOnlyService").sendInOnly("hello");
    }

    @Test
    public void consumeInOutServiceFromBean_new_way() {
        Message responseMsg = newInvoker("ConsumerBean.consumeInOutService").sendInOut("hello");

        Assert.assertEquals("hello", responseMsg.getContent());
    }

    @Test
    public void consumeInOnlyServiceFromBean_Fault_invalid_opertion() {
        try {
            // this should result in a fault
            newInvoker("ConsumerBean.unknownXOp").sendInOut("hello");
            // if we got here, then our negative test failed
            Assert.fail("Invalid operation allowed!");
        } catch (InvocationFaultException infEx) {
            Message faultMsg = infEx.getFaultMessage();
            BeanComponentException e = faultMsg.getContent(BeanComponentException.class);
            Assert.assertEquals("Operation name 'unknownXOp' must resolve to exactly one bean method on bean type '" + 
                    ConsumerBean.class.getName() + "'.", e.getMessage());
        }
    }

    @Test
    public void consumeInOnlyServiceFromBean_Fault_service_exception() {
        try {
            // this should result in a fault
            newInvoker("ConsumerBean.consumeInOutService").sendInOut(new ConsumerException("throw me a remote exception please!!"));
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
