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

package org.switchyard.bus.camel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.camel.impl.DefaultExchange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MockDomain;
import org.switchyard.Property;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.internal.ServiceReferenceImpl;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceMetadataBuilder;
import org.switchyard.metadata.qos.Throttling;
import org.switchyard.spi.Dispatcher;

public class ExchangeDispatcherTest {

    private static final String REQUEST = "REQUEST";
	private ServiceDomain _domain;
    private CamelExchangeBus _provider;
    private SwitchYardCamelContext _camelContext;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
        _camelContext = new SwitchYardCamelContext();
        _camelContext.setServiceDomain(_domain);
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
        ServiceReference reference = new ServiceReferenceImpl(name, new InOnlyService(), null, null);
        ExchangeDispatcher dispatch = _provider.createDispatcher(reference);

        Exchange exchange = new CamelExchange(dispatch, new DefaultExchange(_camelContext), sink);
        exchange.consumer(reference, reference.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        exchange.provider(service, service.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        Message message = exchange.createMessage();
        exchange.send(message);
        Thread.sleep(200);

        Assert.assertEquals(
                message.getContext().getProperty(Exchange.MESSAGE_ID), 
                sink.getLastExchange().getMessage().getContext().getProperty(Exchange.MESSAGE_ID));
    }
    
    @Test
    public void throttle() throws Exception {
        QName name = new QName("testDispatchInOnly");
        final ExchangeSink sink = new ExchangeSink(1000);
        final Service service = new MockService(name, new InOnlyService(), sink);
        final ServiceReference reference = new ServiceReferenceImpl(name, new InOnlyService(), null, null);
        final ExchangeDispatcher dispatch = _provider.createDispatcher(reference);
        
        // Set throttling to 1 per second
        Throttling throttle = new Throttling().setMaxRequests(1);
        ServiceMetadataBuilder.update(reference.getServiceMetadata()).throttling(throttle);
                
        final int NUM_SENDS = 5;
        for (int i = 0; i < NUM_SENDS; i++) {
            new Thread(new Runnable() {
                public void run() {
                    Exchange exchange = dispatch.createExchange(sink, ExchangePattern.IN_ONLY);
                    exchange.consumer(reference, reference.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
                    exchange.provider(service, service.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
                    Message message = exchange.createMessage();
                    exchange.send(message);
                }
            }).start();
        }
        
        Thread.sleep(500);
        Assert.assertTrue("Concurrent requests were not throttled!", 
                sink.getReceivedCount() < NUM_SENDS);
    }

    @Test
    public void testDispatchInOut() throws Exception {
        QName name = new QName("testDispatchInOut");
        // provider handler
        ExchangeSink inHandler = new ExchangeSink(true);
        // consumer handler
        ExchangeSink outHandler = new ExchangeSink();

        Service service = new MockService(name, new InOutService(), inHandler);
        ServiceReference reference = new ServiceReferenceImpl(name, new InOutService(), null, null);
        Dispatcher dispatch = _provider.createDispatcher(reference);

        Exchange exchange = dispatch.createExchange(outHandler, ExchangePattern.IN_OUT);
        exchange.consumer(reference, reference.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        exchange.provider(service, service.getInterface().getOperation(ServiceInterface.DEFAULT_OPERATION));
        Message message = exchange.createMessage();
        exchange.send(message.setContent(REQUEST));
        Thread.sleep(400);

        Exchange lastExchange = outHandler.getLastExchange();
        assertNotNull(lastExchange);

//        assertEquals(REQUEST, lastExchange.getMessage().getContent());

        Property messageId = message.getContext().getProperty(Exchange.MESSAGE_ID);
        assertNotNull("Message id must be available after sending message and receiving response", messageId);
        Property relatesTo = lastExchange.getContext().getProperty(Exchange.RELATES_TO);
        assertNotNull("Relates to must be specified for outgoing message", relatesTo);
        assertEquals("Relates to property should point to in message id", messageId.getValue(), relatesTo.getValue());
    }

    /**
     * Holds a reference to the most recent exchange received by the handler.
     */
    class ExchangeSink extends BaseHandler {

        static final String REPLY = "REPLY";
        private Exchange _lastExchange;
        private boolean _reply;
        private int _wait;
        private AtomicInteger _numExchanges = new AtomicInteger();
        
        ExchangeSink() {
            this(false);
        }
        
        ExchangeSink(int wait) {
            _wait = wait;
        }
        
        ExchangeSink(boolean reply) {
            _reply = reply;
        }

        @Override
        public void handleMessage(Exchange exchange) throws HandlerException {
            _numExchanges.incrementAndGet();
            _lastExchange = exchange;
            
            try {
                Thread.sleep(_wait);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            
            if (_reply) {
                exchange.getContext().setProperty(REPLY, true);
                exchange.send(exchange.createMessage().setContent(REPLY));
            }
        }
        
        public int getReceivedCount() {
            return _numExchanges.get();
        }
        
        Exchange getLastExchange() {
            return _lastExchange;
        }
        
    }
}

