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
