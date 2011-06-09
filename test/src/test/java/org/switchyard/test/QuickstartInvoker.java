/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.test;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quickstart Endpoint invoker utility.
 * <p/>
 * Maybe this code should live in the test utilities and it may eventually be possible
 * to pull the code in this utility class into tests using Arquillian and some test enrichment mechanism.
 * <p/>
 * Uses Apache Camel to perform the endpoint invocations.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class QuickstartInvoker {

    private Map<String, Object> _headers = new HashMap<String, Object>();
    private String _endpointURL;
    private String _responseURL;
    private long _timeout = 10000;

    /**
     * Create a Synchronous invoker.
     * @param endpointURL The target endpoint (URL).
     */
    public QuickstartInvoker(String endpointURL) {
        this._endpointURL = endpointURL;
    }

    /**
     * Create a Asynchronous invoker.
     * @param endpointURL The target endpoint (URL).
     * @param responseURL The asynchronous endpoint (URL).
     */
    public QuickstartInvoker(String endpointURL, String responseURL) {
        this._endpointURL = endpointURL;
        this._responseURL = responseURL;
    }

    /**
     * Create a Quickstart endpoint invoker instance to the specified Quickstart URL.
     * @param endpointURL The quickstart endpoint URL.
     * @return This object.
     */
    public static QuickstartInvoker target(String endpointURL) {
        return new QuickstartInvoker(endpointURL);
    }

    /**
     * Set the asynchronous response endpoint URL.
     * @param responseURL The asynchronous response endpoint URL.
     * @return This object.
     */
    public QuickstartInvoker asyncResponseTo(String responseURL) {
        this._responseURL = responseURL;
        return this;
    }

    /**
     * Set an invocation header.
     * @param name The header name.
     * @param value The header value.
     * @return This object.
     */
    public QuickstartInvoker setHeader(String name, Object value) {
        _headers.put(name, value);
        return this;
    }

    /**
     * Set the response wait timeout.
     * @param timeout Timeout in milliseconds.
     */
    public void setTimeout(long timeout) {
        this._timeout = timeout;
    }

    /**
     * Send a payload to the target endpoint and wait for a response.
     * <p/>
     * The assumption is that there is always a response, received either synchronously from the
     * target endpoint (e.g. HTTP) or asynchronously from the {@link #QuickstartInvoker(String, String) response endpoint}
     * (e.g. a file protocol).
     *
     * @param payload The payload to send to the target endpoint.
     * @param returnType The required return type.
     * @param <T> Return type generic.
     * @return The return value.
     */
    public <T> T send(final Object payload, Class<T> returnType) {
        CamelContext camelContext = new DefaultCamelContext();

        try {
            final MockEndpoint resultEndpoint = new MockEndpoint("mock:result");

            resultEndpoint.setCamelContext(camelContext);
            resultEndpoint.setMinimumExpectedMessageCount(1);
            resultEndpoint.setMinimumResultWaitTime(_timeout);
            camelContext.addEndpoint(resultEndpoint.getEndpointUri(), resultEndpoint);

            camelContext.addRoutes(new RouteBuilder(camelContext) {
                @Override
                public void configure() throws Exception {
                    if (_responseURL == null) {
                        from ("direct:input")
                        .log("Sending: ${body}")
                        .to(_endpointURL)
                        .to(resultEndpoint.getEndpointUri());
                    } else {
                        from ("direct:input")
                        .log("Sending: ${body}")
                        .to(_endpointURL);

                        from (_responseURL)
                        .to(resultEndpoint.getEndpointUri());
                    }
                }
            });

            camelContext.start();
            try {
                ProducerTemplate template = camelContext.createProducerTemplate();
                Endpoint inputEndpoint = camelContext.getEndpoint("direct:input");

                template.send(inputEndpoint, new Processor() {
                    public void process(Exchange exchange) {
                        if (payload instanceof Message) {
                            exchange.setIn((Message) payload);
                        } else {
                            Message in = exchange.getIn();
                            if (!_headers.isEmpty()) {
                                in.getHeaders().putAll(_headers);
                            }
                            in.setBody(payload);
                        }
                    }
                });

                resultEndpoint.await();

                List<Exchange> exchanges = resultEndpoint.getExchanges();

                Assert.assertNotNull(exchanges);
                Assert.assertEquals(1, exchanges.size());

                return exchanges.get(0).getIn().getBody(returnType);
            } finally {
                camelContext.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return null;
        }
    }
}
