/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.hornetq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.test.mixins.HornetQMixIn;

/**
 * Functional test for for {@link HornetQConsumer}.
 * 
 * @author Daniel Bevenius
 */
public class ConsumerJavaDSLTest extends CamelTestSupport {
    
    private static final String DESTINATION = "jms.queue.consumer";
    private static final String MOCK_ENDPOINT_URI = "mock:out";
    private static HornetQMixIn _hornetQMixIn;

    @BeforeClass
    public static void startHornetQ() {
        _hornetQMixIn = new HornetQMixIn();
        _hornetQMixIn.initialize();
    }
    
    @AfterClass
    public static void shutdownHornetQ() {
        _hornetQMixIn.uninitialize();
    }
    
    @Test
    public void consumerTest() throws Exception {
        final String payload = "dummy payload";
        final MockEndpoint endpoint = getMockEndpoint(MOCK_ENDPOINT_URI);
        endpoint.expectedMessageCount(1);
        
        final ClientProducer producer = _hornetQMixIn.getClientSession().createProducer(DESTINATION);
        producer.send(_hornetQMixIn.createMessage(payload));
        
        assertMockEndpointsSatisfied(2000, TimeUnit.MILLISECONDS);
        final String body = getStringPayload(endpoint.getExchanges());
        assertThat(body, is(equalTo(payload)));
        
        HornetQUtil.closeClientProducer(producer);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                final TransportConfiguration transportConfiguration = new TransportConfiguration(InVMConnectorFactory.class.getName());
                final JndiRegistry jndiRegistry = getJndiRegistry(getContext());
                jndiRegistry.bind("transportConfigBean", transportConfiguration);
                
                from("hornetq://" + DESTINATION + "?transportConfiguration=#transportConfigBean")
                .to(MOCK_ENDPOINT_URI);
            }
        };
    }
    
    private JndiRegistry getJndiRegistry(final CamelContext context) {
        final Registry registry = context.getRegistry();
        if (registry instanceof PropertyPlaceholderDelegateRegistry) {
            final PropertyPlaceholderDelegateRegistry ppdr = (PropertyPlaceholderDelegateRegistry) registry;
	        return (JndiRegistry) ppdr.getRegistry();
        }
        else {
	        return (JndiRegistry) context.getRegistry();
        }
    }
    
    private String getStringPayload(final List<Exchange> exchanges) {
        final Exchange exchange = exchanges.get(0);
        return exchange.getIn().getBody(String.class);
    }
    
}
