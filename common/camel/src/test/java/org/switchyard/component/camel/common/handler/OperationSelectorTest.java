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
package org.switchyard.component.camel.common.handler;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.config.model.selector.v1.V1StaticOperationSelectorModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
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
        ProducerTemplate producer = _camelContext.createProducerTemplate();
        InboundHandler<?> handler1 = createInboundHandler("direct://foo", "foo", new V1StaticOperationSelectorModel(SwitchYardNamespace.DEFAULT.uri()).setOperationName("foo"));
        InboundHandler<?> handler2 = createInboundHandler("direct://bar", "bar", new V1StaticOperationSelectorModel(SwitchYardNamespace.DEFAULT.uri()).setOperationName("bar"));

        handler1.start();
        handler2.start();

        MockEndpoint mockEndpoint = _camelContext.getEndpoint("switchyard://dummyService", MockEndpoint.class);
        mockEndpoint.expectedBodiesReceived("FooOperationPayload", "BarOperationPayload");
        mockEndpoint.expectedMessageCount(2);
        producer.sendBody("direct://foo", "FooOperationPayload");
        producer.sendBody("direct://bar", "BarOperationPayload");
        mockEndpoint.assertIsSatisfied();
        List<Exchange> exchanges = mockEndpoint.getReceivedExchanges();
        mockEndpoint.message(0).header(CamelConstants.OPERATION_SELECTOR_HEADER)
            .isInstanceOf(OperationSelector.class).matches(exchanges.get(0));
        mockEndpoint.message(1).header(CamelConstants.OPERATION_SELECTOR_HEADER)
            .isInstanceOf(OperationSelector.class).matches(exchanges.get(1));

        handler1.stop();
        handler2.stop();
    }

}
