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

package org.switchyard.component.bean.omservice.basic;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.*;
import org.switchyard.component.bean.AbstractCDITest;
import org.switchyard.component.bean.omservice.model.OrderRequest;
import org.switchyard.component.bean.omservice.model.OrderResponse;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.internal.ServiceDomains;
import org.switchyard.metadata.InOutOperation;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BasicInOutTest extends AbstractCDITest {

    @Test
    public void test() {
        ServiceDomain domain = ServiceDomains.getDomain();

        // Consume the OM model...
        MockHandler responseConsumer = new MockHandler();
        org.switchyard.Service service = domain.getService(new QName("BasicOrderManagementService"));
        Exchange exchange = domain.createExchange(service, new BaseExchangeContract(new InOutOperation("createOrder")), responseConsumer);

        Message inMessage = exchange.createMessage();
        inMessage.setContent(new OrderRequest("D123", "ABCD"));

        exchange.send(inMessage);

        // wait, since this is async
        responseConsumer.waitForOKMessage();

        OrderResponse response = (OrderResponse) responseConsumer.getMessages().poll().getMessage().getContent();
        Assert.assertEquals("D123", response.orderId);
    }
}
