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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;

/**
 *  Unit tests for the DomainImpl class.
 */
public class DomainImplTest {
     
    private static final QName SERVICE = new QName("Service");
    private ServiceReference _service;
    private DomainImpl _domain;
    
    @Before
    public void setUp() throws Exception {
        _domain = new DomainImpl(new QName("test"),
                new DefaultServiceRegistry(),
                new LocalExchangeBus(),
                null);
        _service = _domain.registerService(SERVICE, new MockHandler());
    }
    
    @Test
    public void testCreateExchange() {
        Exchange inOnly = _domain.createExchange(_service, ExchangeContract.IN_ONLY);
        Assert.assertEquals(ExchangePattern.IN_ONLY, inOnly.getContract().getServiceOperation().getExchangePattern());
        Exchange inOut = _domain.createExchange(_service, ExchangeContract.IN_OUT, new MockHandler());
        Assert.assertEquals(ExchangePattern.IN_OUT, inOut.getContract().getServiceOperation().getExchangePattern());
    }
    
    @Test
    public void testRegisterServiceWithoutInterface() {
        ServiceReference service = _domain.registerService(
                new QName("no-interface"), new MockHandler());
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation(
                ServiceInterface.DEFAULT_OPERATION));
    }
    
    @Test
    public void testRegisterServiceWithInterface() {
        ServiceReference service = _domain.registerService(new QName("my-interface"), 
                new MockHandler(), JavaService.fromClass(MyInterface.class));
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation("myOperation"));
    }
    
    @Test
    public void testGetService() {
        ServiceReference service = _domain.getService(SERVICE);
        Assert.assertNotNull(service);
    }
    
    @Test
    public void testDomainHandler() throws Exception {
        // Add a domain-level handler
        MockHandler provider = new MockHandler();
        CountingHandler counter = new CountingHandler();
        _domain.getHandlerChain().addFirst("counter", counter);
        ServiceReference service = _domain.registerService(
                new QName("Counting"), provider);
        
        // Verify counter is called once for in-only exchange
        Exchange inOnly = _domain.createExchange(service, ExchangeContract.IN_ONLY);
        inOnly.send(new DefaultMessage());
        Assert.assertEquals(1, counter.getCount());
        
        // clear the counter
        counter.clear();
        provider.forwardInToOut();
        
        // Verify counter is called twice for in-out exchange
        Exchange inOut = _domain.createExchange(service, ExchangeContract.IN_OUT, new MockHandler());
        inOut.send(new DefaultMessage().setContent("hello"));
        Assert.assertEquals(2, counter.getCount());
        
    }
    
}

interface MyInterface {
    void myOperation(String msg);
}

class CountingHandler extends BaseHandler {
    private int count;
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        ++count;
    }
    
    public int getCount() {
        return count;
    }
    
    public void clear() {
        count = 0;
    }
}
