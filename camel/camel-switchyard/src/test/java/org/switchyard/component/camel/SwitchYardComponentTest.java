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
package org.switchyard.component.camel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.namespace.QName;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.Binding;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceMetadataBuilder;
import org.switchyard.policy.Policy;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.policy.SecurityPolicy;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Test for {@link SwitchYardComponent}.
 * 
 * @author Daniel Bevenius
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class SwitchYardComponentTest extends SwitchYardComponentTestBase {

    protected String _serviceName = "testServiceName";

    @Test
    public void sendToSwitchyardInOut() throws Exception {
        final String expectedResponse = "replacedContent";
        final String payload = "bajja";

        _serviceDomain.registerService(new QName(_serviceName), new InOutService(), 
            new ResponseService(expectedResponse));
        _serviceDomain.registerServiceReference(new QName(_serviceName), new InOutService("process"));

        final String response = (String) template.requestBody("direct:input", payload);
        assertThat(response, is(equalTo(expectedResponse)));
    }

    @Test
    public void sendToSwitchyardInOnly() throws Exception {
        final String payload = "bajja";
        final MockHandler mockService = new MockHandler();
        _serviceDomain.registerService(new QName(_serviceName), new InOnlyService(), mockService);
        _serviceDomain.registerServiceReference(new QName(_serviceName), new InOnlyService("process"));

        template.sendBody("direct:input", payload);

        assertThat(mockService.getMessages().size(), is(1));
        final String actualPayload = mockService.getMessages().iterator().next().getMessage().getContent(String.class);
        assertThat(actualPayload, is(equalTo(payload)));
    }


    @Test
    public void sendToSwitchyardInOnlyFault() throws Exception {
        final String payload = "bajja";

        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedBodiesReceived(payload);
        final MockHandler mockService = new MockHandler().forwardInToOut();
        _serviceDomain.registerService(new QName(_serviceName), new InOnlyService(), mockService);
        _serviceDomain.registerServiceReference(new QName(_serviceName), new InOnlyService("process"));

        template.sendBody("direct:input", payload);

        assertThat(mockService.getMessages().size(), is(1));
        final String actualPayload = mockService.getMessages().iterator().next().getMessage().getContent(String.class);
        assertThat(actualPayload, is(equalTo(payload)));
        endpoint.assertIsSatisfied();
    }

    @Test
    public void customBindingData() throws Exception {
        List<Policy> policies = Arrays.<Policy>asList(SecurityPolicy.CONFIDENTIALITY);
        final MockHandler mockService = new MockHandler();
        _serviceDomain.registerService(
                new QName(_serviceName),
                new InOnlyService(),
                mockService,
                ServiceMetadataBuilder.create().security(_serviceDomain.getServiceSecurity(null))
                        .requiredPolicies(policies).registrant(new Binding(new ArrayList<BindingModel>())).build());
        _serviceDomain.registerServiceReference(new QName(_serviceName), new InOnlyService("process"));
        _camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("netty://foo").to("switchyard://" + _serviceName + "?operationName=process");
            }
        });
        _camelContext.createProducerTemplate().sendBody("netty://foo", "baja");

        LinkedBlockingQueue<Exchange> msgs = mockService.getMessages();
        Exchange exchange = msgs.iterator().next();
        assertTrue(PolicyUtil.isProvided(exchange, SecurityPolicy.CONFIDENTIALITY));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:input")
                .to("switchyard://" + _serviceName + "?operationName=process")
                .to("mock:result");
            }
        };
    }

    private static class ResponseService extends BaseHandler {
        private final String _response;

        public ResponseService(final String response) {
            this._response = response;
            
        }

        @Override
        public void handleMessage(final Exchange exchange) throws HandlerException {
            final Message responseMessage = exchange.createMessage().setContent(_response);
            exchange.send(responseMessage);
        }
    }

}
