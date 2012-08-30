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

package org.switchyard.bus.camel;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.MockDomain;
import org.switchyard.Scope;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.internal.ServiceReferenceImpl;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.Dispatcher;

public class ExchangeDispatcherTest {

    private ServiceDomain _domain;
    private CamelExchangeBus _provider;
    private SwitchYardCamelContext _camelContext;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
        _camelContext = new SwitchYardCamelContext();
        _provider = new CamelExchangeBus(_camelContext);
        _provider.init(_domain);
        _camelContext.start();
    }
    
    @After
    public void tearDown() throws Exception {
        _camelContext.stop();
    }
    
    @Test
    public void testDispatchInOnly() throws Exception {
        QName name = new QName("testDispatchInOnly");
        ExchangeSink sink = new ExchangeSink();
        Service service = new MockService(name, new InOnlyService(), sink);
        ServiceReference reference = new ServiceReferenceImpl(name, new InOnlyService(), null);
        Dispatcher dispatch = _provider.createDispatcher(reference);
        
        ExchangeImpl exchange = new ExchangeImpl(_domain);
        exchange.consumer(reference, reference.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        exchange.provider(service, service.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        exchange.setOutputDispatcher(dispatch);
        exchange.send(exchange.createMessage());
        Thread.sleep(200);
        
        Assert.assertEquals(
                exchange.getContext().getProperty(Exchange.MESSAGE_ID, Scope.IN), 
                sink.getLastExchange().getContext().getProperty(Exchange.MESSAGE_ID, Scope.IN));
    }
    

    @Test
    public void testDispatchInOut() throws Exception {
        QName name = new QName("testDispatchInOut");
        // provider handler
        ExchangeSink inHandler = new ExchangeSink(true);
        // consumer handler
        ExchangeSink outHandler = new ExchangeSink();
        
        Service service = new MockService(name, new InOutService(), inHandler);
        ServiceReference reference = new ServiceReferenceImpl(name, new InOutService(), null);
        Dispatcher dispatch = _provider.createDispatcher(reference);
        
        ExchangeImpl exchange = new ExchangeImpl(_domain, outHandler);
        exchange.consumer(reference, reference.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        exchange.provider(service, service.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        exchange.setOutputDispatcher(dispatch);
        exchange.send(exchange.createMessage());
        Thread.sleep(400);
        
        Assert.assertNotNull(outHandler.getLastExchange());
        Assert.assertEquals(
                exchange.getContext().getProperty(Exchange.MESSAGE_ID, Scope.IN).getValue(), 
                outHandler.getLastExchange().getContext().getProperty(Exchange.RELATES_TO, Scope.OUT).getValue());
    }
    
}

/**
 * Holds a reference to the most recent exchange received by the handler.
 */
class ExchangeSink extends BaseHandler {
   
    private Exchange _lastExchange;
    private boolean _reply;
    
    ExchangeSink() {
        this(false);
    }
    
    ExchangeSink(boolean reply) {
        _reply = reply;
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        _lastExchange = exchange;
        if (_reply) {
            exchange.send(exchange.createMessage());
        }
    }
    
    Exchange getLastExchange() {
        return _lastExchange;
    }
    
}