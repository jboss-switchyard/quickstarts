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

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.MessageBuilder;
import org.switchyard.MockHandler;
import org.switchyard.ServiceDomain;
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
        MockHandler provider = new MockHandler().forwardInToOut();
        _domain.registerService(serviceName, provider);
        
        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = _domain.createExchange(
                serviceName, ExchangePattern.IN_OUT, consumer);
        exchange.send(MessageBuilder.newInstance().buildMessage());
        
        // wait, since this is async
        provider.waitForMessage();
        consumer.waitForMessage();
    }

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
        exchange.send(MessageBuilder.newInstance().buildMessage());
        
        // wait, since this is async
        provider.waitForMessage();
        consumer.waitForMessage();
        
    }
}
