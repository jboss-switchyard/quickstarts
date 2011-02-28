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

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceDomain;
import org.switchyard.MockDomain;
import org.switchyard.metadata.ExchangeContract;

public class InOutTest {
    
    // where the magic happens
    private ServiceDomain _domain;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }


    /** NEW WAY **/
    @Test
    public void testInOutSuccess() throws Exception {
        final QName serviceName = new QName("inOutSuccess");

        // Provide the service
        MockHandler provider = new MockHandler().forwardInToOut();
        ServiceReference service = _domain.registerService(serviceName, provider);
        
        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = _domain.createExchange(
                service, ExchangeContract.IN_OUT, consumer);
        exchange.send(exchange.createMessage());
        
        // wait, since this is async
        provider.waitForOKMessage();
        consumer.waitForOKMessage();
    }

    @Test
    public void testInOutFault() throws Exception {
        
        final QName serviceName = new QName("inOutFault");
        // Provide the service
        MockHandler provider = new MockHandler().forwardInToFault();
        ServiceReference service = _domain.registerService(serviceName, provider);
        
        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = _domain.createExchange(
                service, ExchangeContract.IN_OUT, consumer);
        exchange.send(exchange.createMessage());
        
        // wait, since this is async
        provider.waitForOKMessage();
        consumer.waitForFaultMessage();
        
    }
}
