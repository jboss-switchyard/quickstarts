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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.namespace.QName;

import org.apache.camel.Processor;
import org.apache.camel.builder.LoggingErrorHandlerBuilder;
import org.apache.camel.spi.RouteContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.bus.camel.handler.ErrorInHandler;
import org.switchyard.bus.camel.handler.ErrorOutHandler;
import org.switchyard.bus.camel.handler.RuntimeErrorFaultHandler;
import org.switchyard.bus.camel.handler.RuntimeErrorInHandler;
import org.switchyard.bus.camel.handler.RuntimeErrorOutHandler;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.internal.ServiceReferenceImpl;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.spi.Dispatcher;

public class CamelExchangeBusTest {

    private final static String TEST_CONTENT = "Some content to sent";

    private CamelExchangeBus _provider;
    private SwitchYardCamelContext _camelContext;
    private MockDomain _domain;

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
    public void testCreateDispatcher() throws Exception {
        // verify that dispatchers can be created for an InOnly service
        ServiceReference inOnly = new ServiceReferenceImpl(
            new QName("inOnly"), new InOnlyService(), _domain);
        assertNotNull(_provider.createDispatcher(inOnly));

        // verify that dispatchers can be created for an InOut service
        ServiceReference inOut = new ServiceReferenceImpl(
            new QName("inOut"), new InOutService(), _domain);
        assertNotNull(_provider.createDispatcher(inOut));
    }

    @Test
    public void testGetDispatcher() throws Exception {
        ServiceReference ref = new ServiceReferenceImpl(
            new QName("testGetDispatcher"), new InOnlyService(), null);
        Dispatcher dispatch = _provider.createDispatcher(ref);

        assertEquals(dispatch, _provider.getDispatcher(ref));
    }

    /**
     * Basic dispatcher test which verifies erroneous response from service.
     */
    @Test
    public void testServiceFault() {
        ServiceReference ref = registerInOutService("inOut", new ErrorExchangeHandler());
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Service is not implemented", exchange);
    }

    /**
     * Basic dispatcher test which verifies erroneous domain handler.
     */
    @Test
    public void testInFault() {
        _domain.getHandlers().add(new ErrorInHandler());

        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertCause("Not implemented", exchange);
    }

    /**
     * Basic dispatcher test which verifies erroneous domain handler.
     */
    @Test
    public void testOutFault() {
        _domain.getHandlers().add(new ErrorOutHandler());

        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Domain handler outgoing error", exchange);
    }

    /**
     * Basic dispatcher test which verifies erroneous domain handler.
     */
    @Test
    public void testInRuntimeFault() {
        _domain.getHandlers().add(new RuntimeErrorInHandler());

        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertCause("Runtime error", exchange);
    }

    /**
     * Basic dispatcher test which verifies erroneous response from service.
     */
    @Test
    public void testOutRuntimeFault() {
        _domain.getHandlers().add(new RuntimeErrorOutHandler());

        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertCause("Outgoing runtime error", exchange);
    }

    /**
     * Basic dispatcher test which verifies erroneous response from service.
     */
    @Test
    public void testFaultFault() {
        _domain.getHandlers().add(new RuntimeErrorFaultHandler());
        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Standard processing exception", exchange);
    }

    @Test
    @Ignore
    public void testErrorHandlerHandling() throws InterruptedException {
        final AtomicBoolean fired = new AtomicBoolean();
        ErrorListener listener = new ErrorListener() {
            @Override
            public void notify(Exchange ex, Throwable e) {
                fired.compareAndSet(false, true);
            }
        };
        _camelContext.getWritebleRegistry().put("custom error listener", listener);
        ServiceReference ref = registerInOutService("inOut", new RuntimeErrorInHandler());
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertTrue(fired.get());
        assertCause("Runtime error", exchange);
    }

    @Test
    public void testCustomErrorHandler() throws InterruptedException {
        final AtomicBoolean fired = new AtomicBoolean();
        _camelContext.getWritebleRegistry().put("custom error handler", new LoggingErrorHandlerBuilder() {
            @Override
            public Processor createErrorHandler(RouteContext routeContext, final Processor processor) {
                fired.compareAndSet(false, true);
                return super.createErrorHandler(routeContext, processor);
            }
        });
        ServiceReference ref = registerInOutService("inOut", new RuntimeErrorInHandler());
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertTrue(fired.get());
        assertCause("Runtime error", exchange);
    }

    protected static void assertNoCause(String message, Exchange exchange) {
        assertEquals(ExchangeState.FAULT, exchange.getState());
        Exception exception = exchange.getMessage().getContent(Exception.class);
        assertNotNull("Exception should not be null", exception);
        assertNull("Cause should be null", exception.getCause());
        assertEquals(message, exception.getMessage());
    }

    protected static void assertCause(String message, Exchange exchange) {
        assertEquals(ExchangeState.FAULT, exchange.getState());
        HandlerException exception = exchange.getMessage().getContent(HandlerException.class);
        assertTrue(exception.isWrapper());
        assertNotNull("Cause should not be null", exception.getCause());
        assertEquals(message, exception.getCause().getMessage());
    }

    private ServiceReference registerInOutService(String name) {
        return registerInOutService(name, new MockHandler().forwardInToOut());
    }

    private ServiceReference registerInOutService(String name, ExchangeHandler handler) {
        ServiceReferenceImpl reference = new ServiceReferenceImpl(new QName(name), new InOutService(), _domain);
        _domain.registerService(new QName(name), new InOutService(), handler);
        reference.setDispatcher(_provider.createDispatcher(reference));
        return reference;
    }

    private Exchange sendMessage(ServiceReference ref, Object content) {
        Exchange exchange = ref.createExchange(new MockHandler());
        exchange.send(exchange.createMessage().setContent(content));
        return exchange;
    }

}
