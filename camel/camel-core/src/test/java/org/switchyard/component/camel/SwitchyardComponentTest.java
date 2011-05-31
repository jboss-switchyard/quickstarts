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
import static org.hamcrest.MatcherAssert.assertThat;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.deploy.ServiceReferences;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardTestCase;

/**
 * Test for {@link SwitchyardComponent}.
 * 
 * @author Daniel Bevenius
 *
 */
public class SwitchyardComponentTest extends SwitchYardTestCase {
    private String _serviceName = "testServiceName";
    private CamelContext _camelContext;
    private ProducerTemplate _template;

    @Before
    public void setup() throws Exception {
        _camelContext = new DefaultCamelContext();
        _camelContext.addRoutes(createRouteBuilder());
        _template = _camelContext.createProducerTemplate();
        _camelContext.start();
        ServiceReferences.clear();
    }
    
    @Test
    public void sendToSwitchyardInOut() throws Exception {
        final String expectedResponse = "replacedContent";
        final String payload = "bajja";
        final InOnlyService inService = new InOnlyService("testServiceName");
        
        final ServiceReference serviceReference = getServiceDomain().registerService(new QName(_serviceName), new ResponseService(expectedResponse), inService);
        ServiceReferences.add(serviceReference.getName(), serviceReference);
        
        final String response = (String) _template.requestBody("direct:input", payload);
        assertThat(response, is(equalTo(expectedResponse)));
    }
    
    @Test
    public void sendToSwitchyardInOnly() throws Exception {
        final String payload = "bajja";
        final MockHandler mockService = new MockHandler();
        final InOnlyService inService = new InOnlyService("testServiceName");
        final ServiceReference serviceReference = getServiceDomain().registerService(new QName(_serviceName), mockService, inService);
        ServiceReferences.add(serviceReference.getName(), serviceReference);
        
        _template.sendBody("direct:input", payload);
        
        assertThat(mockService.getMessages().size(), is(1));
        final String actualPayload = mockService.getMessages().iterator().next().getMessage().getContent(String.class);
        assertThat(actualPayload, is(equalTo(payload)));
    }
    
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder()
        {
            public void configure() throws Exception {
                from("direct:input")
                .to("switchyard://" + _serviceName.toString() + "?operationName=" + _serviceName.toString())
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
