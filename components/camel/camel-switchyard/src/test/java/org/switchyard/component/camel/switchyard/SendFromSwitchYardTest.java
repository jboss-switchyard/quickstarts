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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelContextMapper;
import org.switchyard.component.camel.common.composer.CamelMessageComposer;
import org.switchyard.component.camel.common.handler.OutboundHandler;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.switchyard.util.Composer;
import org.switchyard.component.camel.switchyard.util.Mapper;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class SendFromSwitchYardTest extends SwitchYardComponentTestBase {

    private static final String ENDPOINT_URI = "mock:output";
    private static final String PAYLOAD = "Test Payload";
    private static final String OPERATION_NAME = "foo";
    private MockEndpoint _mock;
    private CamelBindingModel _bindingModel;
    private CompositeReferenceModel _referenceModel = mock(CompositeReferenceModel.class);

    @Before
    public void startUp() {
        _mock = getMockEndpoint(ENDPOINT_URI);
        _bindingModel = mock(CamelBindingModel.class);
        when(_bindingModel.getComponentURI()).thenReturn(URI.create(ENDPOINT_URI));
        when(_bindingModel.getName()).thenReturn("testGateway");
        when(_bindingModel.getReference()).thenReturn(_referenceModel);
    }

    @Test
    public void messageComposerComposeTest() throws InterruptedException {
        _mock.expectedBodiesReceived(Composer.DECOMPOSE_PREFIX + PAYLOAD);
        Exchange exchange = createExchange(new Composer().setContextMapper(new CamelContextMapper()));
        exchange.send(exchange.createMessage().setContent(PAYLOAD));
        _mock.assertIsSatisfied();
    }

    @Test
    public void contextMapperMapToTest() throws InterruptedException {
        _mock.expectedBodiesReceived(PAYLOAD);
        _mock.expectedHeaderReceived(Mapper.PROPERTY, Mapper.VALUE);

        Exchange exchange = createExchange(new CamelMessageComposer().setContextMapper(new Mapper()));
        exchange.send(exchange.createMessage().setContent(PAYLOAD));
        _mock.assertIsSatisfied();
    }

    public Exchange createExchange(MessageComposer<CamelBindingData> messageComposer) {
        QName serviceName = new QName("urn:test", "Service");
        ServiceInterface metadata = new InOnlyService(OPERATION_NAME);

        OutboundHandler handler = new OutboundHandler(_bindingModel, _camelContext, messageComposer, null) {
            {
                setState(State.STARTED);
            }
        };
        _serviceDomain.registerService(serviceName, metadata, handler);
        return _serviceDomain.registerServiceReference(serviceName, metadata).createExchange(OPERATION_NAME);
    }

}
