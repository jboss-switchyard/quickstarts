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
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.spi.Dispatcher;

public class ExchangeDispatcherTest {

    private CamelExchangeBus _provider;

    @Before
    public void setUp() throws Exception {
        _provider = new CamelExchangeBus();
        _provider.init(new MockDomain());
        _provider.start();
    }
    
    @After
    public void tearDown() throws Exception {
        _provider.stop();
    }
    
    @Test
    public void testDispatchInOnly() throws Exception {
        Service service = new MockService(
                new QName("testDispatchInOnly"), new InOnlyService());
        ExchangeSink sink = new ExchangeSink();
        Dispatcher dispatch = _provider.createDispatcher(service, sink);
        
        Exchange exchange = new ExchangeImpl(service.getName(), ExchangeContract.IN_ONLY, dispatch, new MockDomain(), null);
        exchange.send(exchange.createMessage());
        Thread.sleep(200);
        
        Assert.assertEquals(
                exchange.getContext().getProperty(Exchange.MESSAGE_ID, Scope.IN), 
                sink.getLastExchange().getContext().getProperty(Exchange.MESSAGE_ID, Scope.IN));
    }
    

    @Test
    public void testDispatchInOut() throws Exception {
        Service service = new MockService(
                new QName("testDispatchInOut"), new InOutService());
        // provider handler
        ExchangeSink inHandler = new ExchangeSink(true);
        Dispatcher dispatch = _provider.createDispatcher(service, inHandler);
        // consumer handler
        ExchangeSink outHandler = new ExchangeSink();
        
        Exchange exchange = new ExchangeImpl(service.getName(), ExchangeContract.IN_OUT, dispatch, new MockDomain(), outHandler);
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