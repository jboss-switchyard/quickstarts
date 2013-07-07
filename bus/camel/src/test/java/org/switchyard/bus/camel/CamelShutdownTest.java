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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.internal.ServiceReferenceImpl;
import org.switchyard.metadata.InOutService;

public class CamelShutdownTest {

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
    }

    @Test
    public void testShutdownTimeout() throws Exception {
        // set the timeout value to 5 seconds and start the camel context
        _domain.setProperty(SwitchYardCamelContext.SHUTDOWN_TIMEOUT, "5");
        _camelContext.start();
        
        // register a service and send a message
        ServiceReference ref = registerInOutService("inOut", new TakesForeverHandler());
        sendMessage(ref, 120 * 1000);
        
        // Give the sending thread a chance to send the message
        Thread.sleep(1000);
        
        // Stop the context and assert that we haven't waited a long time (default is 30s)
        long beforeStop = System.currentTimeMillis();
        _camelContext.stop();
        long afterStop = System.currentTimeMillis();
        Assert.assertTrue((afterStop - beforeStop) < 6000);
    }
    
    private ServiceReference registerInOutService(String name, ExchangeHandler handler) {
        ServiceReferenceImpl reference = new ServiceReferenceImpl(
                new QName(name), new InOutService(), _domain, null);
        _domain.registerService(new QName(name), new InOutService(), handler);
        reference.setDispatcher(_provider.createDispatcher(reference));
        return reference;
    }

    private void sendMessage(final ServiceReference ref, final Object content) throws Exception {
        new Thread(new Runnable() {
            public void run() {
                Exchange exchange = ref.createExchange(new MockHandler());
                exchange.send(exchange.createMessage().setContent(content));
            }
        }).start();
    }
    
    class TakesForeverHandler extends BaseHandler {
        public void handleMessage(Exchange exchange) throws HandlerException {
            try {
                Thread.sleep(exchange.getMessage().getContent(Integer.class));
            } catch (InterruptedException intEx) {
                System.out.println("TakesForever thread interrupted!");
            }
        }
    }

}
