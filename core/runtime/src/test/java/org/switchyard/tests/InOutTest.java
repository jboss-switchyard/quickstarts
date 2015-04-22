/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.switchyard.tests;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;

public class InOutTest {
    
    private MockDomain _domain;

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
        ServiceReference service = _domain.createInOutService(serviceName, provider);
        
        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = service.createExchange(consumer);
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
        ServiceReference service = _domain.createInOutService(serviceName, provider);
        
        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = service.createExchange(consumer);
        exchange.send(exchange.createMessage());
        
        // wait, since this is async
        provider.waitForOKMessage();
        consumer.waitForFaultMessage();
        
    }
}
