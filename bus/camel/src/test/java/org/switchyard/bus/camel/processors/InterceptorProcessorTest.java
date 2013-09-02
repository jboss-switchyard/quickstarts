package org.switchyard.bus.camel.processors;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.MockDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.handlers.MessageTraceHandler;

public class InterceptorProcessorTest {
    
    private MockDomain _domain;
    private SwitchYardCamelContext _context;
    private Exchange _exchange;
    
    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
        _context = new SwitchYardCamelContext();
        _context.setServiceDomain(_domain);
        _context.start();
        _exchange = new DefaultExchange(_context);
    }

    @Test
    public void exchangeEnabled() throws Exception {
        InterceptProcessor ip = new InterceptProcessor(ExchangeInterceptor.CONSUMER, _domain);
        _exchange.setProperty(MessageTraceHandler.TRACE_ENABLED, true);
        Assert.assertTrue(ip.traceEnabled(_exchange));
        _exchange.setProperty(MessageTraceHandler.TRACE_ENABLED, "true");
        Assert.assertTrue(ip.traceEnabled(_exchange));
        _exchange.setProperty(MessageTraceHandler.TRACE_ENABLED, false);
        Assert.assertFalse(ip.traceEnabled(_exchange));
        _exchange.setProperty(MessageTraceHandler.TRACE_ENABLED, "false");
        Assert.assertFalse(ip.traceEnabled(_exchange));
        _exchange.setProperty(MessageTraceHandler.TRACE_ENABLED, null);
        Assert.assertFalse(ip.traceEnabled(_exchange));
    }
    
    @Test
    public void domainEnabled() throws Exception {
        InterceptProcessor ip = new InterceptProcessor(ExchangeInterceptor.CONSUMER, _domain);
        _domain.setProperty(MessageTraceHandler.TRACE_ENABLED, true);
        Assert.assertTrue(ip.traceEnabled(_exchange));
        _domain.setProperty(MessageTraceHandler.TRACE_ENABLED, "true");
        Assert.assertTrue(ip.traceEnabled(_exchange));
        _domain.setProperty(MessageTraceHandler.TRACE_ENABLED, false);
        Assert.assertFalse(ip.traceEnabled(_exchange));
        _domain.setProperty(MessageTraceHandler.TRACE_ENABLED, "false");
        Assert.assertFalse(ip.traceEnabled(_exchange));
        _domain.setProperty(MessageTraceHandler.TRACE_ENABLED, null);
        Assert.assertFalse(ip.traceEnabled(_exchange));
        _domain.setProperty(MessageTraceHandler.TRACE_ENABLED, "blorg");
        Assert.assertFalse(ip.traceEnabled(_exchange));
        _domain.setProperty(MessageTraceHandler.TRACE_ENABLED, new Object());
        Assert.assertFalse(ip.traceEnabled(_exchange));
    }
}
