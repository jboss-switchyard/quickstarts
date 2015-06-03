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

import static org.junit.Assert.assertEquals;

import org.fusesource.camel.component.sap.SapTransactionalRfcServerEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.sap.model.CamelSapNamespace;
import org.switchyard.component.camel.sap.model.EndpointModel;

/**
 * Test for {@link V2CamelSapTRfcServerModel}.
 */
public class V2CamelSapTRfcServerModelTest extends V1BaseCamelServiceBindingModelTest<V2CamelSapBindingModel, SapTransactionalRfcServerEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-sap-trfc-server-beans.xml";
    private static final String ENDPOINT_TYPE = Constants.TRFC_SERVER;
    private static final String SERVER_NAME = "nplserver";
    private static final String RFC_NAME = "BOOK_FLIGHT";
    private static final String CAMEL_URI = "sap-" + ENDPOINT_TYPE + ":" + SERVER_NAME + ":" + RFC_NAME;

    public V2CamelSapTRfcServerModelTest() {
        super(SapTransactionalRfcServerEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V2CamelSapBindingModel createTestModel() {
        V2RfcServerModel endpoint = new V2RfcServerModel(CamelSapNamespace.V_2_0.uri(), Constants.TRFC_SERVER);
        endpoint.setServerName(SERVER_NAME);
        endpoint.setRfcName(RFC_NAME);
        return new V2CamelSapBindingModel(CamelSapNamespace.V_2_0.uri()).setEndpointModel(endpoint);
    }

    @Override
    protected void createModelAssertions(V2CamelSapBindingModel model) {
        EndpointModel endpoint = model.getEndpointModel();
        assertEquals(ENDPOINT_TYPE, endpoint.getName());
        assertEquals(V2RfcServerModel.class, endpoint.getClass());
        V2RfcServerModel rfcServer = V2RfcServerModel.class.cast(endpoint);
        assertEquals(SERVER_NAME, rfcServer.getServerName());
        assertEquals(RFC_NAME, rfcServer.getRfcName());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
