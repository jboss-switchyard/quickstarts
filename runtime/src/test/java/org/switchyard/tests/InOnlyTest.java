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

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceDomain;
import org.switchyard.MockDomain;
import org.switchyard.metadata.ExchangeContract;

public class InOnlyTest {
    
    // where the magic happens
    private ServiceDomain _domain;
    // event counters used by tests
    private List<Exchange> inEvents = new LinkedList<Exchange>();

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }
    
    @After
    public void tearDown() throws Exception {
        inEvents.clear();
    }
    
    /** NEW WAY **/
    @Test
    public void testInOnlySuccess() throws Exception {
        final QName serviceName = new QName("inOnlySuccess");
        
        // Provide the service
        ExchangeHandler provider = new BaseHandler() {
			public void handleMessage(Exchange event) {
			    inEvents.add(event);
			}
        };
        ServiceReference service = _domain.registerService(serviceName, provider);
        
        // Consume the service
        Exchange exchange = _domain.createExchange(service, ExchangeContract.IN_ONLY);
        exchange.send(exchange.createMessage());
        
        // wait a sec, since this is async
        Thread.sleep(200);
        Assert.assertTrue(inEvents.size() == 1);
    }

}
