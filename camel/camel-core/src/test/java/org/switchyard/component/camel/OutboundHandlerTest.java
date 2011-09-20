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
package org.switchyard.component.camel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Functional test for {@link OutboundHandler}.
 * 
 * @author Daniel Bevenius
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class OutboundHandlerTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    private static MockEndpoint camelEndpoint;

    private ServiceDomain _serviceDomain;

    @ServiceOperation("TargetService")
    private Invoker _targetService;

    private ServiceReference _service;

    /**
     * Exception rule for verifying exceptions.
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setupSwitchyardService() {
        final CamelBindingModel bindingModel = mock(CamelBindingModel.class);
        when(bindingModel.getComponentURI()).thenReturn(URI.create("direct:to"));
        _serviceDomain.registerService(_targetService.getServiceName(), new OutboundHandler(bindingModel.getComponentURI().toString(), context));
        _service = _serviceDomain.getService(_targetService.getServiceName());
    }

    @Test
    public void routeInOnlyToCamel() throws Exception {
        final String payload = "inOnly test string";
        _targetService.contract(new BaseExchangeContract(new InOnlyOperation(_targetService.getServiceName().toString())));
        _targetService.sendInOnly(payload);

        assertThat(camelEndpoint.getReceivedCounter(), is(1));
        final String received = (String) camelEndpoint.getReceivedExchanges().get(0).getIn().getBody();
        assertThat(received, is(equalTo(payload)));
    }

    @Test
    public void verifyThatEsbPropetiesArePassedToCamel() throws Exception {
        final String propertyKey = "testProp";
        final String propertyValue = "dummyValue";
        final ExchangeContract contract = new BaseExchangeContract(new InOnlyOperation(_targetService.getServiceName().toString()));
        final Exchange exchange = _serviceDomain.createExchange(_service, contract);
        exchange.getContext().setProperty(propertyKey, propertyValue);

        exchange.send(exchange.createMessage());

        assertThat(camelEndpoint.getReceivedCounter(), is(1));
        final String actualPropertyValue = (String) camelEndpoint.getReceivedExchanges().get(0).getProperty(propertyKey);
        assertThat(actualPropertyValue, is(propertyValue));
    }

    private Message createMessageWithBody(final Exchange exchange, final Object body) {
        final Message message = exchange.createMessage();
        message.setContent(body);
        return message;
    }

    @Test
    public void routeInOutToCamel() throws Exception {
        final String body = "inOut test string";
        final ExchangeContract contract = new BaseExchangeContract(new InOnlyOperation(_targetService.getServiceName().toString()));
        final Exchange exchange = _serviceDomain.createExchange(_service, contract);
        final Message message = createMessageWithBody(exchange, body);

        exchange.send(message);

        final String payload = (String) exchange.getMessage().getContent();
        assertThat(payload, is(equalTo(body)));
    }

    @Test
    public void throwsIllegalArgumentExceptionIfBindingModelIsNull() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("uri argument must not be null");
        new OutboundHandler(null, mock(CamelContext.class));
    }

    @Test
    public void throwsIllegalArgumentExceptionIfCamelContextIsNull() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("camelContext argument must not be null");
        new OutboundHandler("mockuri", null);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder()
        {
            public void configure() {
                from("direct:to")
                .convertBodyTo(String.class)
                .log("Before Routing to mock:result body: ${body}")
                .to("mock:result");
            }
        };
    }
}