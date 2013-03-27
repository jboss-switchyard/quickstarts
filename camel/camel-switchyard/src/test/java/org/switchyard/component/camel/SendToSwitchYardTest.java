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
package org.switchyard.component.camel;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.util.Composer;
import org.switchyard.component.camel.util.Mapper;
import org.switchyard.component.camel.util.TestService;
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
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
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
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", new V1MessageComposerModel().setClazz(Composer.class.getName()));
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
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", new V1ContextMapperModel().setClazz(Mapper.class.getName()));
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
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", new V1StaticOperationSelectorModel().setOperationName(OPERATION_NAME));
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
        InboundHandler<?> handler = createInboundHandler(_serviceName, "direct:input", new V1StaticOperationSelectorModel().setOperationName("missing"));
        handler.start();

        sendBody("direct:input", PAYLOAD);
        assertTrue(mock.getMessages().isEmpty());
        handler.stop();
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri) {
        return createInboundHandler(serviceName, uri, null, null, null);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri, OperationSelectorModel selectorModel) {
        return createInboundHandler(serviceName, uri, selectorModel, null, null);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri, MessageComposerModel composerModel) {
        return createInboundHandler(serviceName, uri, null, composerModel, null);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, String uri, ContextMapperModel mapperModel) {
        return createInboundHandler(serviceName, uri, null, null, mapperModel);
    }

    protected InboundHandler<?> createInboundHandler(QName serviceName, final String uri, final OperationSelectorModel selectorModel,
        final MessageComposerModel composerModel, final ContextMapperModel mapperModel) {
        V1BaseCamelBindingModel camelBindingModel = new V1BaseCamelBindingModel(_configuration, new Descriptor()) {
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
        return new InboundHandler<V1BaseCamelBindingModel>(camelBindingModel, _camelContext, serviceName);
    }
}
