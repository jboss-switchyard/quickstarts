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
package org.switchyard.component.camel.switchyard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.Property;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.switchyard.util.Composer;
import org.switchyard.component.camel.switchyard.util.Mapper;
import org.switchyard.component.camel.switchyard.util.TestService;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1StaticOperationSelectorModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class SendToSwitchYardTest extends SwitchYardComponentTestBase {

    private static final String OPERATION_NAME = "foo";
    private static final String PAYLOAD = "Test Payload";
    private Configuration _configuration = new ConfigurationPuller().pull(new QName("urn:camel", "binding"));
    private QName _serviceName = new QName("urn:test", "Service");
    private ServiceInterface _metadata = new InOnlyService(OPERATION_NAME);

    @Test
    public void messageComposerComposeTest() {
        MockHandler mock = new MockHandler().forwardInToOut();
        _serviceDomain.registerService(_serviceName, _metadata, mock);
        _serviceDomain.registerServiceReference(_serviceName, _metadata);
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", "input",
                new V1MessageComposerModel(SwitchYardNamespace.DEFAULT.uri()).setClazz(Composer.class.getName()));
        handler.start();

        sendBody("direct:input", PAYLOAD);
        mock.waitForOKMessage();
        List<Exchange> exchanges = new ArrayList<Exchange>();
        mock.getMessages().drainTo(exchanges);
        assertEquals(1, exchanges.size());
        assertEquals(Composer.COMPOSE_PREFIX + PAYLOAD, exchanges.get(0).getMessage().getContent());

        handler.stop();
    }

    @Test
    public void contextMapperMapToTest() {
        MockHandler mock = new MockHandler().forwardInToOut();
        _serviceDomain.registerService(_serviceName, _metadata, mock);
        _serviceDomain.registerServiceReference(_serviceName, _metadata);
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", "direct",
                new V1ContextMapperModel(SwitchYardNamespace.DEFAULT.uri()).setClazz(Mapper.class.getName()));
        handler.start();

        sendBody("direct:input", PAYLOAD);
        mock.waitForOKMessage();
        List<Exchange> exchanges = new ArrayList<Exchange>();
        mock.getMessages().drainTo(exchanges);
        assertEquals(1, exchanges.size());
        Exchange exchange = exchanges.get(0);
        Property property = exchange.getContext().getProperty(Mapper.PROPERTY);
        assertNotNull(property);
        assertEquals(Mapper.VALUE, property.getValue());

        handler.stop();
    }

    @Test
    public void operationSelectorTest() {
        MockHandler mock = new MockHandler().forwardInToOut();
        _serviceDomain.registerService(_serviceName, _metadata, mock);
        _serviceDomain.registerServiceReference(_serviceName, _metadata);
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", "input",
                new V1StaticOperationSelectorModel(SwitchYardNamespace.DEFAULT.uri()).setOperationName(OPERATION_NAME));
        handler.start();

        sendBody("direct:input", PAYLOAD);
        mock.waitForOKMessage();
        List<Exchange> exchanges = new ArrayList<Exchange>();
        mock.getMessages().drainTo(exchanges);
        assertEquals(1, exchanges.size());
        assertEquals(OPERATION_NAME, exchanges.get(0).getContract().getProviderOperation().getName());

        handler.stop();
    }

    @Test
    public void wrongOperationSelectorTest() {
        MockHandler mock = new MockHandler().forwardInToOut();
        JavaService metadata = JavaService.fromClass(TestService.class);
        _serviceDomain.registerService(_serviceName, metadata, mock);
        _serviceDomain.registerServiceReference(_serviceName, metadata);
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", "input",
                new V1StaticOperationSelectorModel(SwitchYardNamespace.DEFAULT.uri()).setOperationName("missing"));
        handler.start();

        sendBody("direct:input", PAYLOAD);
        assertTrue(mock.getMessages().isEmpty());
        handler.stop();
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri, final String name) {
        return createInboundHandler(serviceName, uri, name, null, null, null);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri, final String name, OperationSelectorModel selectorModel) {
        return createInboundHandler(serviceName, uri, name, selectorModel, null, null);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri, final String name, MessageComposerModel composerModel) {
        return createInboundHandler(serviceName, uri, name, null, composerModel, null);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri, final String name, ContextMapperModel mapperModel) {
        return createInboundHandler(serviceName, uri, name, null, null, mapperModel);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, final String uri, final String name, final OperationSelectorModel selectorModel,
        final MessageComposerModel composerModel, final ContextMapperModel mapperModel) {
        V1BaseCamelBindingModel camelBindingModel = new V1BaseCamelBindingModel(_configuration, new Descriptor()) {
            @Override
            public String getName() {
                return name;
            }
            @Override
            public URI getComponentURI() {
                return URI.create(uri);
            }
            @Override
            public OperationSelectorModel getOperationSelector() {
                return selectorModel;
            }
            @Override
            public MessageComposerModel getMessageComposer() {
                return composerModel;
            }
            @Override
            public ContextMapperModel getContextMapper() {
                return mapperModel;
            }
        };
        return new InboundHandler<V1BaseCamelBindingModel>(camelBindingModel, _camelContext, serviceName, null);
    }
}
