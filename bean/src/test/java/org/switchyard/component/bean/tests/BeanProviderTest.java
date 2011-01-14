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
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.MockHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bean.AbstractCDITest;
import org.switchyard.component.bean.BeanServiceMetadata;
import org.switchyard.internal.ServiceDomains;

/*
 * Assorted methods for testing a CDI bean providing a service in SwitchYard.
 */
public class BeanProviderTest extends AbstractCDITest {

    @Test
    public void invokeOneWayProviderWithInOnly() {
        
        ServiceDomain domain = ServiceDomains.getDomain();

        Exchange exchange = domain.createExchange(new QName("OneWay"), ExchangePattern.IN_ONLY);

        BeanServiceMetadata.setOperationName(exchange, "oneWay");
        
        Message inMessage = MessageBuilder.newInstance().buildMessage();
        inMessage.setContent("hello");

        exchange.send(inMessage);
    }
    
    @Test
    public void invokeRequestResponseProviderWithInOut() {
        final String ECHO_MSG = "hello";
        
        ServiceDomain domain = ServiceDomains.getDomain();

        MockHandler responseConsumer = new MockHandler();
        Exchange exchange = domain.createExchange(new QName("RequestResponse"), ExchangePattern.IN_OUT, responseConsumer);

        BeanServiceMetadata.setOperationName(exchange, "reply");
        
        Message inMessage = MessageBuilder.newInstance().buildMessage();
        inMessage.setContent(ECHO_MSG);

        exchange.send(inMessage);

        // wait, since this is async
        responseConsumer.waitForOKMessage();

        Assert.assertEquals(ECHO_MSG,
                responseConsumer.getMessages().poll().getMessage().getContent());
    }
}
