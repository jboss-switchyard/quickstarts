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

package org.switchyard.internal;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.MockHandler;
import org.switchyard.Service;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.JavaService;
import org.switchyard.metadata.ServiceInterface;

/**
 *  Unit tests for the DomainImpl class.
 */
public class DomainImplTest {
     
    private static final QName SERVICE = new QName("Service");
    private Service _service;
    private DomainImpl _domain;
    
    @Before
    public void setUp() throws Exception {
        _domain = new DomainImpl("test",
                new DefaultServiceRegistry(),
                new DefaultEndpointProvider(),
                null);
        _service = _domain.registerService(SERVICE, new MockHandler());
    }
    
    @Test
    public void testCreateExchange() {
        Exchange inOnly = _domain.createExchange(_service, ExchangeContract.IN_ONLY);
        Assert.assertEquals(ExchangePattern.IN_ONLY, inOnly.getContract().getServiceOperation().getExchangePattern());
        Exchange inOut = _domain.createExchange(_service, ExchangeContract.IN_OUT);
        Assert.assertEquals(ExchangePattern.IN_OUT, inOut.getContract().getServiceOperation().getExchangePattern());
    }
    
    @Test
    public void testRegisterServiceWithoutInterface() {
        Service service = _domain.registerService(
                new QName("no-interface"), new MockHandler());
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation(
                ServiceInterface.DEFAULT_OPERATION));
    }
    
    @Test
    public void testRegisterServiceWithInterface() {
        Service service = _domain.registerService(new QName("my-interface"), 
                new MockHandler(), JavaService.fromClass(MyInterface.class));
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation("myOperation"));
    }
    
    @Test
    public void testGetService() {
        Service service = _domain.getService(SERVICE);
        Assert.assertNotNull(service);
    }
    
}

interface MyInterface {
    void myOperation(String msg);
}
