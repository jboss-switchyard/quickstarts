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

import java.util.EventObject;

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
import org.switchyard.event.EventObserver;
import org.switchyard.internal.ServiceReferenceImpl;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.runtime.event.ExchangeInitiatedEvent;
import org.switchyard.spi.Dispatcher;

public class ExchangeEventsTest {

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
    public void testExchangeEventsForInOnly() throws Exception {
        EventCounter counter = new EventCounter();
        _domain.addEventObserver(counter, ExchangeInitiatedEvent.class);
        _domain.addEventObserver(counter, ExchangeCompletionEvent.class);
        
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

        Assert.assertEquals(1, counter.initiatedCount);
        Assert.assertEquals(1, counter.completedCount);
    }

    @Test
    public void testExchangeEventsForInOut() throws Exception {
        EventCounter counter = new EventCounter();
        _domain.addEventObserver(counter, ExchangeInitiatedEvent.class);
        _domain.addEventObserver(counter, ExchangeCompletionEvent.class);
        
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

        Assert.assertEquals(1, counter.initiatedCount);
        Assert.assertEquals(1, counter.completedCount);
    }

    class EventCounter implements EventObserver {
        int initiatedCount;
        int completedCount;
        public void notify(EventObject event) {
            if (event instanceof ExchangeInitiatedEvent) {
                ++initiatedCount;
            } else if (event instanceof ExchangeCompletionEvent) {
                ++completedCount;
            }
        }
    };
    
    /**
     * Holds a reference to the most recent exchange received by the handler.
     */
    class ExchangeSink extends BaseHandler {

        static final String REPLY = "REPLY";
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
                exchange.getContext().setProperty(REPLY, true);
                exchange.send(exchange.createMessage().setContent(REPLY));
            }
        }
        
        Exchange getLastExchange() {
            return _lastExchange;
        }
        
    }
}
