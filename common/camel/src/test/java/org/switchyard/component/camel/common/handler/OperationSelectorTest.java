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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.TypeConversionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.TypeConverterSupport;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.selector.CamelOperationSelector;
import org.switchyard.config.model.selector.XPathOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1StaticOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1XPathOperationSelectorModel;
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

    @Test
    public void testOperationSelectorOnNettyChannelBuffer() throws Exception {
        String payload = "<body><operation>greet</operation><name>Tomas</name></body>";
        ProducerTemplate producer = _camelContext.createProducerTemplate();
        XPathOperationSelectorModel operationSelectorModel = new V1XPathOperationSelectorModel(SwitchYardNamespace.DEFAULT.uri());
        operationSelectorModel.setExpression("//body/operation");
        InboundHandler<?> handler = createInboundHandler("direct://xpath", "xpath", operationSelectorModel);
        handler.start();
        
        try {
            // BigEndianHeapChannelBuffer->String converter is provided by camel-netty component
            _camelContext.getTypeConverterRegistry()
                         .addTypeConverter(String.class, BigEndianHeapChannelBuffer.class, new MyConverter()); 
            MockEndpoint mockEndpoint = _camelContext.getEndpoint("switchyard://dummyService", MockEndpoint.class);
            producer.sendBody("direct://xpath", new BigEndianHeapChannelBuffer(payload.getBytes()));
            Exchange exchange = mockEndpoint.getExchanges().get(0);
            CamelOperationSelector selector = new CamelOperationSelector(operationSelectorModel);
            QName operation = selector.selectOperation(new CamelBindingData(exchange.getIn()));
            Assert.assertNotNull(operation);
            Assert.assertEquals("greet", operation.getLocalPart());
        } finally {
            handler.stop();
        }
    }

    private class MyConverter extends TypeConverterSupport {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T convertTo(Class<T> type, Exchange exchange, Object value)
                throws TypeConversionException {
            BigEndianHeapChannelBuffer buffer = BigEndianHeapChannelBuffer.class.cast(value);
            return (T) new String(buffer.array());
        }
    }
}
