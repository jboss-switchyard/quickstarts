package org.switchyard.bus.camel;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.MockDomain;
import org.switchyard.Scope;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContextImpl;
import org.switchyard.internal.ServiceReferenceImpl;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.InOnlyService;

public class CamelCompositeContextTest {

    private CamelExchangeBus _provider;
    private SwitchYardCamelContextImpl _camelContext;
    private MockDomain _domain;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
        _camelContext = new SwitchYardCamelContextImpl();
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
    public void testCopyFromExchange() throws Exception {
        ServiceReference inOnly = new ServiceReferenceImpl(
            new QName("exchange-copy"), new InOnlyService(), _domain, null);
        ExchangeDispatcher dispatch = _provider.createDispatcher(inOnly);
        
        Exchange ex = dispatch.createExchange(null, ExchangePattern.IN_ONLY);
        Context ctx = ex.getContext();
        ctx.setProperty("message-prop", "message-val", Scope.MESSAGE);
        ctx.setProperty("exchange-prop", "exchange-val", Scope.EXCHANGE).addLabels(BehaviorLabel.TRANSIENT.label());
        Assert.assertEquals(ctx.getProperty("message-prop", Scope.MESSAGE).getValue(), "message-val");
        Assert.assertEquals(ctx.getProperty("exchange-prop", Scope.EXCHANGE).getValue(), "exchange-val");
        Assert.assertTrue(ctx.getProperty("exchange-prop", Scope.EXCHANGE).getLabels().contains(BehaviorLabel.TRANSIENT.label()));
        
        // Merge the context from ex into the context for ex2
        Exchange ex2 = dispatch.createExchange(null, ExchangePattern.IN_ONLY);
        Context ctx2 = ex2.getContext();
        ctx.mergeInto(ctx2);
        Assert.assertNotNull(ctx2.getProperty("message-prop", Scope.MESSAGE));
        Assert.assertNull(ctx2.getProperty("exchange-prop", Scope.EXCHANGE));
    }
}
