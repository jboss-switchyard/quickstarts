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
import org.switchyard.Service;
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
        Service service = _domain.registerService(serviceName, provider);
        
        // Consume the service
        Exchange exchange = _domain.createExchange(service, ExchangeContract.IN_ONLY);
        exchange.send(exchange.createMessage());
        
        // wait a sec, since this is async
        Thread.sleep(200);
        Assert.assertTrue(inEvents.size() == 1);
    }

}
