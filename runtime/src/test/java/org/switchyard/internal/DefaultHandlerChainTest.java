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

package org.switchyard.internal;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.spi.Dispatcher;

public class DefaultHandlerChainTest {
    
    private HandlerChain _chain;
    
    @Before
    public void setUp() {
        _chain = new DefaultHandlerChain();
    }
    
    @Test
    public void faultChainContinuesOnFault() throws Exception {
        // This goes first and throws an exception - bad handler!
        ExchangeHandler badHandler = new BaseHandler() {
            @Override
            public void handleFault(Exchange exchange) {
                throw new RuntimeException("oops!");
            }
        };
        // This goes second and should still be called despite the bad handler
        MockHandler goodHandler = new MockHandler();
        
        _chain.addFirst("first", badHandler);
        _chain.addLast("second", goodHandler);

        MockDomain domain = new MockDomain();
        ServiceReference reference = domain.createInOnlyService(new QName("bar"));
        Dispatcher dispatch = domain.getBus().createDispatcher(reference);
        Exchange ex = new ExchangeImpl(domain, dispatch).consumer(reference, new InOnlyOperation("foo"));
        _chain.handleFault(ex);
        Assert.assertNotNull(goodHandler.waitForFaultMessage());
    }
    
    @Test
    public void testReplace() {
        MockHandler m1 = new MockHandler();
        MockHandler m2 = new MockHandler();
        MockHandler m3 = new MockHandler();
        MockHandler m4 = new MockHandler();
        
        _chain.addFirst("1", m1);

        Assert.assertFalse(_chain.replace("5", m2));
        Assert.assertTrue(_chain.replace("1", m2));
        
        Assert.assertTrue(_chain.getHandlers().contains(m2));
        Assert.assertFalse(_chain.getHandlers().contains(m1));
        
        // clean up
        _chain.remove("1");
        Assert.assertEquals(0, _chain.getHandlers().size());
        
        // test replace with multiple handlers
        _chain.addFirst("1", m1);
        _chain.addFirst("2", m2);
        _chain.addFirst("3", m3);
        _chain.replace("2", m4);
        
        Assert.assertEquals(m4, _chain.getHandlers().get(1));
    }
}



