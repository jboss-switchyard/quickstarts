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

package org.switchyard.bus.hornetq;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.ServiceReference;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.internal.DefaultHandlerChain;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.spi.Dispatcher;

public class HornetQDispatcherTest {

    private HornetQBus _provider;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(HornetQBus.WORK_DIR, "target/hornetQ");
        _provider = new HornetQBus(config);
        _provider.start();
    }
    
    @After
    public void tearDown() throws Exception {
        _provider.stop();
    }
    
    @Test
    public void testDispatchInOnly() throws Exception {
        ServiceReference service = new MockServiceReference(
                new QName("testDispatchInOnly"),new InOnlyService());
        HandlerChain inHandlers = new DefaultHandlerChain();
        ExchangeSink sink = new ExchangeSink();
        inHandlers.addLast("in", sink);
        Dispatcher dispatch = _provider.createDispatcher(service, inHandlers);
        
        Exchange exchange = new ExchangeImpl(service, ExchangeContract.IN_ONLY, dispatch, null);
        exchange.send(null);
        Thread.sleep(200);
        
        Assert.assertEquals(exchange, sink.getLastExchange());
    }
    

    @Test
    public void testDispatchInOut() throws Exception {
        ServiceReference service = new MockServiceReference(
                new QName("testDispatchInOut"),new InOutService());
        // provider handlers
        HandlerChain inHandlers = new DefaultHandlerChain();
        ExchangeSink inHandler = new ExchangeSink(true);
        inHandlers.addLast("in", inHandler);
        // consumer handlers
        Dispatcher dispatch = _provider.createDispatcher(service, inHandlers);
        HandlerChain outHandlers = new DefaultHandlerChain();
        ExchangeSink outHandler = new ExchangeSink();
        outHandlers.addLast("out", outHandler);
        
        Exchange exchange = new ExchangeImpl(service, ExchangeContract.IN_OUT, dispatch, outHandlers);
        exchange.send(null);
        Thread.sleep(400);
        
        Assert.assertNotNull(outHandler.getLastExchange());
        Assert.assertEquals(exchange, outHandler.getLastExchange());
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
            exchange.send(null);
        }
    }
    
    Exchange getLastExchange() {
        return _lastExchange;
    }
    
}