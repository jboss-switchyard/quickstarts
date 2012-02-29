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

package org.switchyard.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerChain;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.metadata.ExchangeContract;

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
        
        _chain.handleFault(new ExchangeImpl(null, ExchangeContract.IN_ONLY, null, new MockDomain(), null));
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



