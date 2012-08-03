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

import java.util.EventObject;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.event.EventObserver;
import org.switchyard.event.ReferenceRegistrationEvent;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;

/**
 *  Unit tests for the DomainImpl class.
 */
public class DomainImplTest {
     
    private static final QName IN_ONLY_SERVICE = new QName("InOnlyService");
    private static final QName IN_OUT_SERVICE = new QName("InOutService");
    private ServiceReference _inOnlyReference;
    private ServiceReference _inOutReference;
    private DomainImpl _domain;
    
    @Before
    public void setUp() throws Exception {
        _domain = new DomainImpl(new QName("test"));
        _domain.registerService(IN_ONLY_SERVICE, new InOnlyService(), new MockHandler());
        _domain.registerService(IN_OUT_SERVICE, new InOutService(), new MockHandler());
        _inOnlyReference = _domain.registerServiceReference(IN_ONLY_SERVICE, new InOnlyService());
        _inOutReference = _domain.registerServiceReference(IN_OUT_SERVICE, new InOutService());
    }
    
    @Test
    public void testCreateExchange() {
        Exchange inOnly = _inOnlyReference.createExchange();
        Assert.assertEquals(ExchangePattern.IN_ONLY, inOnly.getContract().getServiceOperation().getExchangePattern());
        Exchange inOut = _inOutReference.createExchange(new MockHandler());
        Assert.assertEquals(ExchangePattern.IN_OUT, inOut.getContract().getServiceOperation().getExchangePattern());
    }
    
    @Test
    public void testRegisterServiceWithoutInterface() {
        Service service = _domain.registerService(
                new QName("no-interface"), null, new MockHandler());
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation(
                ServiceInterface.DEFAULT_OPERATION));
    }
    
    @Test
    public void testRegisterServiceWithInterface() {
        Service service = _domain.registerService(new QName("my-interface"), 
                JavaService.fromClass(MyInterface.class), new MockHandler());
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation("myOperation"));
    }
    
    /*
    @Test
    public void testDomainHandler() throws Exception {
        MockDomain testDomain = new MockDomain();
        // Add a domain-level handler
        CountingHandler counter = new CountingHandler();
        testDomain.getHandlerChain().addFirst("counter", counter);
        ServiceReference inOnly = testDomain.createInOnlyService(new QName("CountIn"));
        ServiceReference inOut = testDomain.createInOutService(
                new QName("CountInOut"), new MockHandler().forwardInToOut());
        
        // Verify counter is called once for in-only exchange
        Exchange ex1 = inOnly.createExchange();
        ex1.send(new DefaultMessage());
        Assert.assertEquals(1, counter.getCount());
        
        // clear the counter
        counter.clear();
        
        // Verify counter is called twice for in-out exchange
        Exchange ex2 = inOut.createExchange(new MockHandler());
        ex2.send(new DefaultMessage().setContent("hello"));
        Assert.assertEquals(2, counter.getCount());
    }
    */
    
    @Test
    public void testGetEventPublisher() {
        // Test to make sure event manager is initialized in domain
        Assert.assertNotNull(_domain.getEventPublisher());
    }
    
    @Test
    public void testAddObserver() {
        CountingEventObserver obs = new CountingEventObserver();
        _domain.addEventObserver(obs, ReferenceRegistrationEvent.class);
        _domain.getEventPublisher().publish(new ReferenceRegistrationEvent(_inOnlyReference));
        Assert.assertEquals(1, obs.count);
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

class CountingEventObserver implements EventObserver {
    
    public int count;

    public void notify(EventObject event) {
        ++count;
    }
}
