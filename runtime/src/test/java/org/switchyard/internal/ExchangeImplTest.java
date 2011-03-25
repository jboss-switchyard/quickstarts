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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.BaseInvocationContract;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InvocationContract;
import org.switchyard.metadata.ServiceOperation;

/**
 *  Unit tests for the ExchangeImpl class.
 */
public class ExchangeImplTest {
    
    private ServiceDomain _domain;
    
    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }
    
    @Test
    public void testSendFaultOnNewExchange() {
        Exchange exchange = new ExchangeImpl(null, ExchangeContract.IN_ONLY, null, null);
        try {
            exchange.sendFault(exchange.createMessage());
            Assert.fail("Sending a fault on a new exchange is not allowed");
        } catch (IllegalStateException illEx) {
            return;
        }
    }
    
    @Test
    public void testPhaseIsNullOnNewExchange() {
        Exchange exchange = new ExchangeImpl(null, ExchangeContract.IN_ONLY, null, null);
        Assert.assertNull(exchange.getPhase());
    }
    
    @Test
    public void testPhaseIsInAfterInputMessage() {
        ServiceReference service = _domain.registerService(
                new QName("InPhase"), new MockHandler());
        Exchange exchange = _domain.createExchange(service, ExchangeContract.IN_ONLY);
        exchange.send(exchange.createMessage());
        Assert.assertEquals(ExchangePhase.IN, exchange.getPhase());
    }
    
    @Test
    public void testPhaseIsOutAfterOutputMessage() {
        ServiceReference service = _domain.registerService(
                new QName("OutPhase"), new MockHandler().forwardInToOut());
        MockHandler replyHandler = new MockHandler();
        Exchange exchange = _domain.createExchange(
                service, ExchangeContract.IN_OUT, replyHandler);
        exchange.send(exchange.createMessage());
        replyHandler.waitForOKMessage();
        Assert.assertEquals(ExchangePhase.OUT, exchange.getPhase());
    }
    
    @Test
    public void testPhaseIsOutAfterFaultMessage() {
        ServiceReference service = _domain.registerService(
                new QName("FaultPhase"), new MockHandler().forwardInToFault());
        MockHandler replyHandler = new MockHandler();
        Exchange exchange = _domain.createExchange(
                service, ExchangeContract.IN_OUT, replyHandler);
        exchange.send(exchange.createMessage());
        replyHandler.waitForFaultMessage();
        Assert.assertEquals(ExchangePhase.OUT, exchange.getPhase());
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
        
        ServiceReference service = _domain.registerService(serviceName, provider);
        Exchange exchange = _domain.createExchange(
                service, ExchangeContract.IN_OUT, consumer);
        Message inMsg = exchange.createMessage();
        inMsg.setContent(inMsgContent);
        exchange.send(inMsg);
    }

    @Test
    public void testExceptionOnSendOnFaultExchange() throws Exception {

        final QName serviceName = new QName("testExceptionOnSendOnFaultExchange");
        // Provide the service
        MockHandler provider = new MockHandler().forwardInToFault();
        ServiceReference service = _domain.registerService(serviceName, provider);

        // Consume the service
        MockHandler consumer = new MockHandler();
        Exchange exchange = _domain.createExchange(
                service, ExchangeContract.IN_OUT, consumer);
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
    public void testExchangeContractServiceOperationProvided() throws Exception {
        MockHandler provider = new MockHandler();
        ServiceReference service = _domain.registerService(new QName("serviceX"), provider);

        try {
            _domain.createExchange(service, null);
        } catch(IllegalArgumentException e) {
            Assert.assertEquals("null 'contract' arg.", e.getMessage());
        }

        try {
            _domain.createExchange(service, new ExchangeContract() {
                @Override
                public InvocationContract getInvokerInvocationMetaData() {
                    return null;
                }

                @Override
                public ServiceOperation getServiceOperation() {
                    return null;
                }
            });
        } catch(IllegalArgumentException e) {
            Assert.assertEquals("Invalid 'contract' arg.  No invoker invocation metadata defined on the contract instance.", e.getMessage());
        }

        try {
            _domain.createExchange(service, new ExchangeContract() {
                @Override
                public InvocationContract getInvokerInvocationMetaData() {
                    return new BaseInvocationContract();
                }

                @Override
                public ServiceOperation getServiceOperation() {
                    return null;
                }
            });
        } catch(IllegalArgumentException e) {
            Assert.assertEquals("Invalid 'contract' arg.  No ServiceOperation defined on the contract instance.", e.getMessage());
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
        
        ServiceReference service = _domain.registerService(serviceName, provider);

        try {
            // Don't provide a consumer...
            Exchange exchange = _domain.createExchange(service, ExchangeContract.IN_OUT);
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
        ServiceReference service = _domain.registerService(serviceName, provider);

        // Don't provide a consumer...
        Exchange exchange = _domain.createExchange(service, ExchangeContract.IN_ONLY);

        exchange.send(exchange.createMessage());
    }

}
