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
import org.switchyard.MockDomain;
import org.switchyard.ServiceReference;

public class InOnlyTest {
    
    private MockDomain _domain;
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
    
    @Test
    public void testInOnlySuccess() throws Exception {
        final QName serviceName = new QName("inOnlySuccess");
        
        // Provide the service
        ExchangeHandler provider = new BaseHandler() {
			public void handleMessage(Exchange event) {
			    inEvents.add(event);
			}
        };

        ServiceReference service = _domain.createInOnlyService(serviceName, provider);
        
        // Consume the service
        Exchange exchange = service.createExchange();
        exchange.send(exchange.createMessage());
        
        // wait a sec, since this is async
        Thread.sleep(200);
        Assert.assertTrue(inEvents.size() == 1);
    }

}
