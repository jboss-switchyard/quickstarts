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

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.MockHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bean.AbstractCDITest;
import org.switchyard.component.bean.BeanComponentException;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.internal.ServiceDomains;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;

/*
 * Assorted methods for testing a CDI bean consuming a service in SwitchYard.
 */
public class BeanConsumerTest extends AbstractCDITest {

    @Test
    public void consumeInOnlyServiceFromBean() {
        ServiceDomain domain = ServiceDomains.getDomain();

        org.switchyard.Service service = domain.getService(new QName("ConsumerBean"));
        Exchange exchange = domain.createExchange(service, new BaseExchangeContract(new InOnlyOperation("consumeInOnlyService")));

        Message inMessage = exchange.createMessage().setContent("hello");

        exchange.send(inMessage);
    }


    @Test
    public void consumeInOutServiceFromBean() {
        ServiceDomain domain = ServiceDomains.getDomain();

        MockHandler responseConsumer = new MockHandler();
        org.switchyard.Service service = domain.getService(new QName("ConsumerBean"));
        Exchange exchange = domain.createExchange(service, new BaseExchangeContract(new InOutOperation("consumeInOutService")), responseConsumer);

        Message inMessage = exchange.createMessage().setContent("hello");

        exchange.send(inMessage);

        responseConsumer.waitForOKMessage();
        Assert.assertEquals("hello", responseConsumer.getMessages().peek().getMessage().getContent());
    }

    @Test
    public void consumeInOnlyServiceFromBean_Fault_invalid_opertion() {
        ServiceDomain domain = ServiceDomains.getDomain();

        MockHandler responseConsumer = new MockHandler();
        org.switchyard.Service service = domain.getService(new QName("ConsumerBean"));
        Exchange exchange = domain.createExchange(service, new BaseExchangeContract(new InOutOperation("unknownXOp")), responseConsumer);

        Message inMessage = exchange.createMessage().setContent("hello");

        exchange.send(inMessage);

        responseConsumer.waitForFaultMessage();

        Exchange faultExchange = responseConsumer.getFaults().peek();
        BeanComponentException e = faultExchange.getMessage().getContent(BeanComponentException.class);
        Assert.assertEquals("Operation name 'unknownXOp' must resolve to exactly one bean method on bean type '" + ConsumerBean.class.getName() + "'.", e.getMessage());
    }

    @Test
    public void consumeInOnlyServiceFromBean_Fault_service_exception() {
        ServiceDomain domain = ServiceDomains.getDomain();

        MockHandler responseConsumer = new MockHandler();
        org.switchyard.Service service = domain.getService(new QName("ConsumerBean"));
        Exchange exchange = domain.createExchange(service, new BaseExchangeContract(new InOutOperation("consumeInOutService")), responseConsumer);

        Message inMessage = exchange.createMessage().setContent(
                new ConsumerException("throw me a remote exception please!!"));

        exchange.send(inMessage);

        responseConsumer.waitForFaultMessage();

        Exchange faultExchange = responseConsumer.getFaults().peek();
        Object response = faultExchange.getMessage().getContent();
        Assert.assertTrue(response instanceof BeanComponentException);
        Assert.assertEquals("Invocation of operation 'consumeInOutService' on bean component '" + ConsumerBean.class.getName() + "' failed with exception.  See attached cause.", ((BeanComponentException) response).getMessage());
        Assert.assertTrue((((BeanComponentException) response).getCause()).getCause() instanceof ConsumerException);
        Assert.assertEquals("remote-exception-received", ((BeanComponentException) response).getCause().getCause().getMessage());
    }
}
