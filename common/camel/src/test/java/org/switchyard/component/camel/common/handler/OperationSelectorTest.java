/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.camel.common.handler;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.selector.v1.V1StaticOperationSelectorModel;
import org.switchyard.selector.OperationSelector;

/**
 * Test of operation selector stuff.
 */
public class OperationSelectorTest extends InboundHandlerTestBase {

    @Before
    public void startUpTest() throws Exception {
        _camelContext.start();
    }

    @After
    public void tearDown() throws Exception {
        _camelContext.stop();
    }

    @Test
    public void operationSelectorProcessor() throws Exception {
        _camelContext.addComponent("switchyard", new MockComponent());
        ProducerTemplate producer = _camelContext.createProducerTemplate();
        InboundHandler handler1 = createInboundHandler("direct://foo", new V1StaticOperationSelectorModel().setOperationName("foo"));
        InboundHandler handler2 = createInboundHandler("direct://bar", new V1StaticOperationSelectorModel().setOperationName("bar"));

        handler1.start();
        handler2.start();

        MockEndpoint mockEndpoint = _camelContext.getEndpoint("switchyard://dummyService?namespace=urn:foo", MockEndpoint.class);
        mockEndpoint.expectedBodiesReceived("FooOperationPayload", "BarOperationPayload");
        mockEndpoint.expectedMessageCount(2);
        producer.sendBody("direct://foo", "FooOperationPayload");
        producer.sendBody("direct://bar", "BarOperationPayload");
        mockEndpoint.assertIsSatisfied();
        List<Exchange> exchanges = mockEndpoint.getReceivedExchanges();
        mockEndpoint.message(0).header(InboundHandler.OPERATION_SELECTOR_REF)
            .isInstanceOf(OperationSelector.class).matches(exchanges.get(0));
        mockEndpoint.message(1).header(InboundHandler.OPERATION_SELECTOR_REF)
            .isInstanceOf(OperationSelector.class).matches(exchanges.get(1));

        handler1.stop();
        handler2.stop();
    }

}
