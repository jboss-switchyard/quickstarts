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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.camel.Processor;
import org.apache.camel.builder.LoggingErrorHandlerBuilder;
import org.apache.camel.spi.RouteContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.switchyard.ErrorListener;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.bus.camel.handler.ErrorInterceptor;
import org.switchyard.bus.camel.handler.RuntimeErrorInHandler;
import org.switchyard.bus.camel.handler.RuntimeErrorInterceptor;
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
            new QName("inOnly"), new InOnlyService(), _domain, null);
        assertNotNull(_provider.createDispatcher(inOnly));

        // verify that dispatchers can be created for an InOut service
        ServiceReference inOut = new ServiceReferenceImpl(
            new QName("inOut"), new InOutService(), _domain, null);
        assertNotNull(_provider.createDispatcher(inOut));
    }

    @Test
    public void testGetDispatcher() throws Exception {
        ServiceReference ref = new ServiceReferenceImpl(
            new QName("testGetDispatcher"), new InOnlyService(), null, null);
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
     * Verify consumer callback is called when fault occurs on InOnly.
     */
    @Test
    public void testFaultReportedOnInOnly() {
        ServiceReference ref = registerInOnlyService("inOut", new ErrorExchangeHandler());
        MockHandler consumer = new MockHandler();
        Exchange exchange = ref.createExchange(consumer);
        exchange.send(exchange.createMessage().setContent("test"));
        
        Assert.assertEquals(1, consumer.waitForFaultMessage().getFaults().size());
    }

    /**
     * Basic dispatcher test which verifies erroneous interceptor.
     */
    @Test
    public void testBeforeProviderErrorInOut() {
        ErrorInterceptor interceptor = new ErrorInterceptor(false, ExchangeInterceptor.PROVIDER);
        _camelContext.getWritebleRegistry().put("interceptor", interceptor);

        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Error before on target Provider", exchange);
        Assert.assertEquals(2, interceptor.getCount());
    }
    
    /**
     * Basic dispatcher test which verifies erroneous interceptor.
     */
    @Test
    public void testAfterProviderErrorInOut() {
        ErrorInterceptor interceptor = new ErrorInterceptor(true, ExchangeInterceptor.PROVIDER);
        _camelContext.getWritebleRegistry().put("interceptor", interceptor);
        
        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Error after on target Provider", exchange);
        Assert.assertEquals(2, interceptor.getCount());
    }
    
    /**
     * Basic dispatcher test which verifies erroneous interceptor.
     */
    @Test
    public void testBeforeProviderErrorInOnly() {
        ErrorInterceptor interceptor = new ErrorInterceptor(false, ExchangeInterceptor.PROVIDER);
        _camelContext.getWritebleRegistry().put("interceptor", interceptor);

        ServiceReference ref = registerInOnlyService("inOnly", new MockHandler());
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Error before on target Provider", exchange);
        Assert.assertEquals(2, interceptor.getCount());
    }
    
    /**
     * Basic dispatcher test which verifies erroneous interceptor.
     */
    @Test
    public void testAfterProviderErrorInOnly() {
        ErrorInterceptor interceptor = new ErrorInterceptor(true, ExchangeInterceptor.PROVIDER);
        _camelContext.getWritebleRegistry().put("interceptor", interceptor);

        ServiceReference ref = registerInOnlyService("inOnly", new MockHandler());
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Error after on target Provider", exchange);
        Assert.assertEquals(2, interceptor.getCount());
    }

    /**
     * Basic dispatcher test which verifies erroneous domain handler.
     */
    @Test
    public void testInRuntimeFault() {
        RuntimeErrorInterceptor interceptor = new RuntimeErrorInterceptor(
                false, ExchangeInterceptor.PROVIDER);
        _camelContext.getWritebleRegistry().put("interceptor", interceptor);

        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertCause("RuntimeException before on target Provider", exchange);
        Assert.assertEquals(2, interceptor.getCount());
    }

    /**
     * Basic dispatcher test which verifies erroneous response from service.
     */
    @Test
    public void testOutRuntimeFault() {
        RuntimeErrorInterceptor interceptor = new RuntimeErrorInterceptor(
                true, ExchangeInterceptor.PROVIDER);
        _camelContext.getWritebleRegistry().put("interceptor", interceptor);

        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertCause("RuntimeException after on target Provider", exchange);
        Assert.assertEquals(2, interceptor.getCount());
    }

    /**
     * Basic dispatcher test which verifies erroneous response from service.
     */
    @Test
    public void testFaultFault() {
        ErrorInterceptor beforeFault = new ErrorInterceptor(false, ExchangeInterceptor.PROVIDER);
        ErrorInterceptor afterFault = new ErrorInterceptor(true, ExchangeInterceptor.PROVIDER);
        _camelContext.getWritebleRegistry().put("beforeFault", beforeFault);
        _camelContext.getWritebleRegistry().put("afterFault", afterFault);
        ServiceReference ref = registerInOutService("inOut");
        Exchange exchange = sendMessage(ref, TEST_CONTENT);

        assertNoCause("Error before on target Provider", exchange);
        Assert.assertEquals(2, beforeFault.getCount());
        Assert.assertEquals(2, afterFault.getCount());
    }

    @Test
    public void testErrorListener() throws InterruptedException {
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

    @Test @Ignore
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
        // SWITCHYARD-1634
        assertNotNull(exchange.getContext().getProperty(Exchange.MESSAGE_ID));
        assertNotNull(exchange.getContext().getProperty(Exchange.RELATES_TO));
    }

    protected static void assertCause(String message, Exchange exchange) {
        assertEquals(ExchangeState.FAULT, exchange.getState());
        HandlerException exception = exchange.getMessage().getContent(HandlerException.class);
        assertTrue(exception.isWrapper());
        assertNotNull("Cause should not be null", exception.getCause());
        assertEquals(message, exception.getCause().getMessage());
        // SWITCHYARD-1634
        assertNotNull(exchange.getContext().getProperty(Exchange.MESSAGE_ID));
        assertNotNull(exchange.getContext().getProperty(Exchange.RELATES_TO));
    }

    private ServiceReference registerInOutService(String name) {
        return registerInOutService(name, new MockHandler().forwardInToOut());
    }

    private ServiceReference registerInOnlyService(String name, ExchangeHandler handler) {
        ServiceReferenceImpl reference = new ServiceReferenceImpl(new QName(name), new InOnlyService(), _domain, null);
        _domain.registerService(new QName(name), new InOnlyService(), handler);
        reference.setDispatcher(_provider.createDispatcher(reference));
        return reference;
    }
    
    private ServiceReference registerInOutService(String name, ExchangeHandler handler) {
        ServiceReferenceImpl reference = new ServiceReferenceImpl(
                new QName(name), new InOutService(), _domain, null);
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
