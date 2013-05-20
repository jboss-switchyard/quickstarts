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
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.event.EventObserver;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.runtime.event.ExchangeInitiatedEvent;
import org.switchyard.spi.Dispatcher;
import org.switchyard.transform.BaseTransformer;

/**
 *  Unit tests for the ExchangeImpl class.
 */
public class ExchangeImplTest {
    
    private MockDomain _domain;
	private Dispatcher _dispatch;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
        ServiceReference reference = _domain.createInOnlyService(new QName("foo"));
        _dispatch = _domain.getBus().createDispatcher(reference);
    }
    
    @Test
    public void testSendFaultOnNewExchange() {
        Exchange exchange = new ExchangeImpl(_domain, _dispatch);
        try {
            exchange.sendFault(exchange.createMessage());
            Assert.fail("Sending a fault on a new exchange is not allowed");
        } catch (IllegalStateException illEx) {
            return;
        }
    }
    
    @Test
    public void testPhaseIsNullOnNewExchange() {
        Exchange exchange = new ExchangeImpl(_domain, _dispatch);
        Assert.assertNull(exchange.getPhase());
    }
    
    @Test
    public void testPhaseIsInAfterInputMessage() {
        ServiceReference service = _domain.createInOnlyService(new QName("InPhase"));
        Exchange exchange = service.createExchange();
        exchange.send(exchange.createMessage());
        Assert.assertEquals(ExchangePhase.IN, exchange.getPhase());
    }
    
    @Test
    public void testPhaseIsOutAfterOutputMessage() {
        MockHandler replyHandler = new MockHandler();
        ServiceReference service = _domain.createInOutService(
                new QName("OutPhase"), new MockHandler().forwardInToOut());
        Exchange exchange = service.createExchange(replyHandler);
        exchange.send(exchange.createMessage());
        replyHandler.waitForOKMessage();
        Assert.assertEquals(ExchangePhase.OUT, exchange.getPhase());
    }
    
    @Test
    public void testPhaseIsOutAfterFaultMessage() {
        MockHandler replyHandler = new MockHandler();
        ServiceReference service = _domain.createInOutService(
                new QName("FaultPhase"), new MockHandler().forwardInToFault());
        Exchange exchange = service.createExchange(replyHandler);
        exchange.send(exchange.createMessage());
        replyHandler.waitForFaultMessage();
        Assert.assertEquals(ExchangePhase.OUT, exchange.getPhase());
    }
    
    @Test
    public void testMessageIdSetOnSend() {
        ServiceReference service = _domain.createInOnlyService(new QName("IdTest"));
        Exchange exchange = service.createExchange();
        exchange.send(exchange.createMessage());
        Assert.assertNotNull(exchange.getMessage().getContext().getProperty(Exchange.MESSAGE_ID));
    }
    
    @Test
    public void testRelatesToSetOnReply() {
        ServiceReference service = _domain.createInOutService(
            new QName("ReplyTest"), new MockHandler().forwardInToOut());
        MockHandler replyHandler = new MockHandler();
        Exchange exchange = service.createExchange(replyHandler);
        Message message = exchange.createMessage();
        exchange.send(message);

        String requestId = message.getContext().getPropertyValue(Exchange.MESSAGE_ID);
        String replyId = exchange.getMessage().getContext().getPropertyValue(Exchange.MESSAGE_ID);
        String replyRelatesTo = exchange.getMessage().getContext().getPropertyValue(Exchange.RELATES_TO);

        Assert.assertEquals(requestId, replyRelatesTo);
        Assert.assertFalse(requestId.equals(replyId));
    }

    @Test
    public void testNullSend() {
        Exchange exchange = new ExchangeImpl(_domain, _dispatch);
        try {
            exchange.send(null);
            Assert.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid null 'message' argument in method call.", e.getMessage());
        }
    }

    @Test
    public void testNullSendFault() {
        Exchange exchange = new ExchangeImpl(_domain, _dispatch);
        try {
            exchange.sendFault(null);
            Assert.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid null 'message' argument in method call.", e.getMessage());
        }
    }
    
    @Test
    public void testExchangeInitiatedEvent() {
        EventCounter counter = new EventCounter();
        _domain.addEventObserver(counter, ExchangeInitiatedEvent.class);
        
        // send in-only and check the count
        ServiceReference inOnlyService = _domain.createInOnlyService(new QName("ExchangeInitiatedEvent-1"));
        Exchange inOnly = inOnlyService.createExchange();
        inOnly.send(inOnly.createMessage());
        Assert.assertEquals(1, counter.initiatedCount);
        
        // reset count
        counter.initiatedCount = 0;
        
        // send in-out and check the count
        ServiceReference inOutService = _domain.createInOutService(
                new QName("ExchangeInitiatedEvent-2"), new MockHandler().forwardInToOut());
        Exchange inOut = inOutService.createExchange(new MockHandler());
        inOut.send(inOut.createMessage());
        Assert.assertEquals(1, counter.initiatedCount);
    }

    @Test
    public void testExchangeCompletedEvent() {
        EventCounter counter = new EventCounter();
        _domain.addEventObserver(counter, ExchangeCompletionEvent.class);
        
        // send in-only and check the count
        ServiceReference inOnlyService = _domain.createInOnlyService(new QName("ExchangeCompleteEvent-1"));
        Exchange inOnly = inOnlyService.createExchange();
        inOnly.send(inOnly.createMessage());
        Assert.assertEquals(1, counter.completedCount);
        
        // reset count
        counter.completedCount = 0;
        
        // send in-out and check the count
        ServiceReference inOutService = _domain.createInOutService(
                new QName("ExchangeCompleteEvent-2"), new MockHandler().forwardInToOut());
        Exchange inOut = inOutService.createExchange(new MockHandler());
        inOut.send(inOut.createMessage());
        Assert.assertEquals(1, counter.completedCount);
    }
    
    @Test
    public void testAllExchangeEventsReceived() throws Exception {
        EventCounter counter = new EventCounter();
        _domain.addEventObserver(counter, ExchangeInitiatedEvent.class);
        _domain.addEventObserver(counter, ExchangeCompletionEvent.class);
        
        // send 10 in-only messages and check the counters
        for (int i = 0; i < 10; i++) {
            ServiceReference inOnlyService = _domain.createInOnlyService(new QName("ExchangeEvent-0" + i));
            Exchange inOnly = inOnlyService.createExchange();
            inOnly.send(inOnly.createMessage());
        }
        
        Assert.assertEquals(10, counter.initiatedCount);
        Assert.assertEquals(10, counter.completedCount);
        
        // initialize counters
        counter.initiatedCount = 0;
        counter.completedCount = 0;
        
        // send 10 in-out and check the count
        for (int i = 0; i < 10; i++) {
            ServiceReference inOutService = _domain.createInOutService(
                    new QName("ExchangeEvent-1" + i), new MockHandler().forwardInToOut());
            Exchange inOut = inOutService.createExchange(new MockHandler());
            inOut.send(inOut.createMessage());
        }
        
        Assert.assertEquals(10, counter.initiatedCount);
        Assert.assertEquals(10, counter.completedCount);
    }

    /**
     * Make sure that the current message is set correctly when an exchange
     * is sent.
     */
    @Test
    public void testGetMessage() throws Exception {
        
        final QName serviceName = new QName("bleh");
        final String inMsgContent = "in message";
        final String outMsgContent = "out message";
        
        // create a handler to test that the in and out content match
        // expected result from getMessage()
        ExchangeHandler provider = new BaseHandler() {
            public void handleMessage(Exchange exchange) {
                Assert.assertEquals(
                		exchange.getMessage().getContent(), 
                        inMsgContent);
                
                Message outMsg = exchange.createMessage();
                outMsg.setContent(outMsgContent);
                try {
                	exchange.send(outMsg);
                }
                catch (Exception ex) {
                    Assert.fail(ex.toString());
                }
            }
        };

        ExchangeHandler consumer = new BaseHandler() {
            public void handleMessage(Exchange exchange) {
                Assert.assertEquals(
                		exchange.getMessage().getContent(), 
                        outMsgContent);
            }
        };

        ServiceReference service = _domain.createInOutService(serviceName, provider);
        Exchange exchange = service.createExchange(consumer);
        Message inMsg = exchange.createMessage();
        inMsg.setContent(inMsgContent);
        exchange.send(inMsg);
    }

    @Test
    public void testExceptionOnSendOnFaultExchange() throws Exception {

        final QName serviceName = new QName("testExceptionOnSendOnFaultExchange");
        // Provide the service
        MockHandler provider = new MockHandler().forwardInToFault();
        ServiceReference service = _domain.createInOutService(serviceName, provider);

        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = service.createExchange(consumer);
        exchange.send(exchange.createMessage());

        // wait, since this is async
        provider.waitForOKMessage();
        consumer.waitForFaultMessage();

        // Now try send another message on the Exchange... should result in an IllegalStateException...
        try {
            exchange.send(exchange.createMessage());
        } catch(IllegalStateException e) {
            Assert.assertEquals("Exchange instance is in a FAULT state.", e.getMessage());
        }
    }

    @Test
    public void testExceptionOnNoConsumerOnInOut() throws Exception {

        QName serviceName = new QName("testNoNPEOnNoConsumer");
        MockHandler provider = new MockHandler() {
            @Override
            public void handleMessage(Exchange exchange) throws HandlerException {
                throw new HandlerException("explode");
            }
        };

        ServiceReference service = _domain.createInOutService(serviceName, provider);

        try {
            // Don't provide a consumer...
            service.createExchange();
            Assert.fail("Should not be able to create an InOut without specifying a reply handler");
        } catch (RuntimeException e) {
            // exception expected
        }
    }

    @Test
    public void testNoNPEOnInOnlyFault() throws Exception {

        QName serviceName = new QName("testNoNPEOnNoConsumer");
        MockHandler provider = new MockHandler() {
            @Override
            public void handleMessage(Exchange exchange) throws HandlerException {
                throw new HandlerException("explode");
            }
        };

        ServiceReference service = _domain.createInOnlyService(serviceName, provider);

        // Don't provide a consumer...
        Exchange exchange = service.createExchange();

        exchange.send(exchange.createMessage());
    }
    
    @Test
    public void testFaultWithNoHandler() throws Exception {

        final QName serviceName = new QName("testFaultWithNoHandler");
        // Provide the service
        MockHandler provider = new MockHandler() {
            @Override
            public void handleMessage(Exchange exchange) throws HandlerException {
                throw new HandlerException("Fault With No Handler!");
            }
        };
        ServiceReference service = _domain.createInOnlyService(serviceName, provider);

        // Consume the service
        Exchange exchange = service.createExchange();
        exchange.send(exchange.createMessage());

        // Make sure the exchange is in fault status
        Assert.assertEquals(ExchangeState.FAULT, exchange.getState());
    }
    
    @Test
    public void testFaultTransformSequence() throws Exception {
        final QName serviceName = new QName("testFaultTransformSequence");
        // Provide the service
        MockHandler provider = new MockHandler() {
            @Override
            public void handleMessage(Exchange exchange) throws HandlerException {
                Message fault = exchange.createMessage();
                fault.setContent(new Exception("testFaultTransformSequence"));
                exchange.sendFault(fault);
            }
        };
        InOutOperation providerContract = new InOutOperation("faultOp", 
                JavaService.toMessageType(String.class),   // input
                JavaService.toMessageType(String.class),   // output
                JavaService.toMessageType(Exception.class));  // fault
        InOutOperation consumerContract = new InOutOperation("faultOp", 
                JavaService.toMessageType(String.class),   // input
                JavaService.toMessageType(String.class),   // output
                JavaService.toMessageType(String.class));  // fault
        _domain.registerService(serviceName, new InOutService(providerContract), provider);
        _domain.getTransformerRegistry().addTransformer(new ExceptionToStringTransformer());
        ServiceReference service = _domain.registerServiceReference(serviceName, new InOutService(consumerContract));

        // Consume the service
        Exchange exchange = service.createExchange(new MockHandler());
        exchange.send(exchange.createMessage());

        // Make sure the exchange is in fault status
        Assert.assertEquals(String.class, exchange.getMessage().getContent().getClass());
        Assert.assertEquals(exchange.getMessage().getContent(), "testFaultTransformSequence");
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

}

class ExceptionToStringTransformer extends BaseTransformer<Exception, String> {

    @Override
    public String transform(Exception from) {
        return from.getMessage();
    }
    
}
