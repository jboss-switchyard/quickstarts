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

package org.switchyard.admin.base;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.mockito.Mockito;
import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.Property;
import org.switchyard.admin.ComponentService;
import org.switchyard.deploy.ComponentNames;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Test of message metrics collection on various levels.
 */
public class MessageMetricsCollectionTest extends SwitchYardBuilderTestBase {

    private static final String OPERATION_NAME = "greet";
    private static final QName TEST_SERVICE = new QName("urn:m1app:example:1.0", "M1AppService");
    private static final QName TEST_PROMOTED_SERVICE = new QName("urn:m1app:example:1.0", "SimpleService");
    private static final QName TEST_REFERENCE = ComponentNames.qualify(TEST_PROMOTED_SERVICE, new QName("urn:m1app:example:1.0", "anotherService"));
    private static final String TEST_GATEWAY = "_M1AppService_sca_1";

    public MessageMetricsCollectionTest() throws Exception {
        super();
    }

    @Test
    public void testSwitchyardLevelCollection() {
        Exchange ex = createMock();
        defaultExpectations(ex);

        _builder.notify(new ExchangeCompletionEvent(ex));

        assertEquals(1, _switchYard.getMessageMetrics().getSuccessCount());
        assertEquals(10.0, _switchYard.getMessageMetrics().getAverageProcessingTime(), 0);
    }

    @Test
    public void testOperationLevelCollection() {
        Exchange ex = createMock();
        defaultExpectations(ex);

        Mockito.when(ex.getContract().getProviderOperation().getName()).thenReturn(OPERATION_NAME);

        _builder.notify(new ExchangeCompletionEvent(ex));

        assertEquals(1, _switchYard.getMessageMetrics().getSuccessCount());
        assertEquals(10.0, _switchYard.getMessageMetrics().getAverageProcessingTime(), 0);
        ComponentService componentService = _switchYard.getApplication(TEST_APP).getComponentService(TEST_PROMOTED_SERVICE);
        assertEquals(10.0, componentService.getMessageMetrics().getAverageProcessingTime(), 0);
        assertEquals(10.0, componentService.getServiceOperation(OPERATION_NAME).getMessageMetrics().getAverageProcessingTime(), 0);
        assertEquals(10.0, _switchYard.getApplication(TEST_APP).getService(TEST_SERVICE).getGateway(TEST_GATEWAY).getMessageMetrics().getAverageProcessingTime(), 0);
    }

    private Exchange createMock() {
        return mock(Exchange.class, Mockito.RETURNS_DEEP_STUBS);
    }

    private void defaultExpectations(Exchange ex) {
        when(ex.getProvider().getName()).thenReturn(TEST_SERVICE);
        when(ex.getConsumer().getName()).thenReturn(TEST_REFERENCE);
        when(ex.getState()).thenReturn(ExchangeState.OK);
        Property property = mock(Property.class);
        when(property.getValue()).thenReturn(new Long(10));
        when(ex.getContext().getProperty(ExchangeCompletionEvent.EXCHANGE_DURATION)).thenReturn(property);
        when(ex.getContext().getPropertyValue(ExchangeCompletionEvent.GATEWAY_NAME)).thenReturn(TEST_GATEWAY);
    }
}
