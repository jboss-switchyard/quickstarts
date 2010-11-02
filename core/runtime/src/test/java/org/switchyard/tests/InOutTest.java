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

package org.switchyard.tests;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.After;
import org.switchyard.*;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeEvent;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.MessageBuilder;
import org.switchyard.ServiceDomain;
import org.switchyard.event.ExchangeFaultEvent;
import org.switchyard.event.ExchangeInEvent;
import org.switchyard.event.ExchangeOutEvent;
import org.switchyard.internal.ServiceDomains;

public class InOutTest {
    
    // where the magic happens
    private ServiceDomain _domain;

    @Before
    public void setUp() throws Exception {
        _domain = ServiceDomains.getDomain();
    }


    /** NEW WAY **/
    @Test
    public void testInOutSuccess() throws Exception {
        final QName serviceName = new QName("inOutSuccess");

        // Provide the service
        MockHandler provider = new MockHandler();
        _domain.registerService(serviceName, provider);
        
        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = _domain.createExchange(
                serviceName, ExchangePattern.IN_OUT, consumer);
        exchange.sendIn(MessageBuilder.newInstance().buildMessage());
        
        // wait, since this is async
        provider.waitForIn();
        consumer.waitForOut();
    }
    
    /** OLD WAY
    @Test
    public void testInOutSuccess() throws Exception {
        final QName serviceName = new QName("inOutSuccess");
        // Provide the service
        ExchangeChannel providerChannel = _domain.createChannel();
        providerChannel.getHandlerChain().addFirst("provider", 
                new BaseHandler() {
                    public void exchangeIn(ExchangeInEvent event) {
                        inEvents.add(event);
                        Exchange inEx = event.getExchange();
                        inEx.setOut(MessageBuilder.newInstance().buildMessage());
                        event.getChannel().send(inEx);
                    }
        });
        _domain.registerService(serviceName, providerChannel);
        
        // Consume the service
        ExchangeChannel consumerChannel = _domain.createChannel();
        consumerChannel.getHandlerChain().addFirst("consumer", 
                new BaseHandler() {
                    public void exchangeOut(ExchangeOutEvent event) {
                        outEvents.add(event);
                    }
        });
        Exchange exchange = consumerChannel.createExchange(ExchangePattern.IN_OUT);
        exchange.setService(serviceName);
        consumerChannel.send(exchange);
        
        // wait a sec, since this is async
        Thread.sleep(200);
        Assert.assertTrue(inEvents.size() == 1);
        Assert.assertTrue(outEvents.size() == 1);
    }
    */

    @Test
    public void testInOutFault() throws Exception {
        
        final QName serviceName = new QName("inOutFault");
        // Provide the service
        MockHandler provider = new MockHandler().forwardInToFault();
        _domain.registerService(serviceName, provider);
        
        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = _domain.createExchange(
                serviceName, ExchangePattern.IN_OUT, consumer);
        exchange.sendIn(MessageBuilder.newInstance().buildMessage());
        
        // wait, since this is async
        provider.waitForIn();
        consumer.waitForFault();
        
    }
}
