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
package org.switchyard.component.camel.sap.model.v2;

import static junit.framework.Assert.assertEquals;

import org.fusesource.camel.component.sap.SAPEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.sap.model.CamelSapNamespace;

/**
 * Test for {@link V2CamelSapBindingModel}.
 */
public class V2CamelSapConsumerBindingModelTest extends V1BaseCamelServiceBindingModelTest<V2CamelSapBindingModel, SAPEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-sap-consumer-binding-beans.xml";
    private static final String CAMEL_URI = "sap:server:nplserver:BOOK_FLIGHT";
    private static final String SERVER = "nplserver";
    private static final String RFC_NAME = "BOOK_FLIGHT";
    private static final Boolean TRANSACTED = Boolean.FALSE;

    public V2CamelSapConsumerBindingModelTest() {
        super(SAPEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V2CamelSapBindingModel createTestModel() {
        return new V2CamelSapBindingModel(CamelSapNamespace.V_2_0.uri())
                        .setServer(SERVER)
                        .setRfcName(RFC_NAME);

    }

    @Override
    protected void createModelAssertions(V2CamelSapBindingModel model) {
        assertEquals(SERVER, model.getServer());
        assertEquals(RFC_NAME, model.getRfcName());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
