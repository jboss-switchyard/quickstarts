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
package org.switchyard.component.camel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

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
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
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
        // endpoint.allMessages().
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:input")
                .to("switchyard://" + _serviceName.toString() + "?operationName=process")
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
