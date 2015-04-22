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

import java.util.EventObject;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.MockHandler;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.event.EventObserver;
import org.switchyard.event.ReferenceRegistrationEvent;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;

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
        inOnly.consumer(null, new InOnlyOperation("foo"));
        Assert.assertEquals(ExchangePattern.IN_ONLY, inOnly.getContract().getConsumerOperation().getExchangePattern());
        Exchange inOut = _inOutReference.createExchange(new MockHandler());
        inOut.consumer(null, new InOutOperation("foo"));
        Assert.assertEquals(ExchangePattern.IN_OUT, inOut.getContract().getConsumerOperation().getExchangePattern());
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
    
    @Test
    public void testGetSetProperties() {
        Assert.assertNull(_domain.getProperty("foo"));
        _domain.setProperty("foo", "foo-value");
        Assert.assertEquals("foo-value", _domain.getProperty("foo"));
        Assert.assertEquals("foo-value", _domain.getProperties().get("foo"));
        
        // confirm that we can't set properties via getProperties()
        try {
            _domain.getProperties().put("bar", "bar-value");
            Assert.fail("Should not be able to modify map returned from ServiceDomain.getProperties()");
        } catch (Exception ex) {
            Assert.assertEquals(UnsupportedOperationException.class, ex.getClass());
        }
    }
    
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
