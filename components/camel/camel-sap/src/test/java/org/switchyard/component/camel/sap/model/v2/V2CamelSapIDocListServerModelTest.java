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

import org.fusesource.camel.component.sap.SapTransactionalIDocListServerEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.sap.model.CamelSapNamespace;
import org.switchyard.component.camel.sap.model.EndpointModel;

/**
 * Test for {@link V2CamelSapIDocListServerModel}.
 */
public class V2CamelSapIDocListServerModelTest extends V1BaseCamelServiceBindingModelTest<V2CamelSapBindingModel, SapTransactionalIDocListServerEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-sap-idoclist-server-beans.xml";
    private static final String ENDPOINT_TYPE = Constants.IDOCLIST_SERVER;
    private static final String SERVER_NAME = "nplserver";
    private static final String IDOC_TYPE = "BOOK_FLIGHT";
    private static final String IDOC_TYPE_EXTENSION = "IDOC_TYPE_EXTENSION";
    private static final String SYSTEM_RELEASE = "SYSTEM_RELEASE";
    private static final String APPLICATION_RELEASE = "APPLICATION_RELEASE";
    private static final String CAMEL_URI = "sap-" + ENDPOINT_TYPE + ":" + SERVER_NAME + ":" + IDOC_TYPE
            + ":" + IDOC_TYPE_EXTENSION + ":" + SYSTEM_RELEASE + ":" + APPLICATION_RELEASE;

    public V2CamelSapIDocListServerModelTest() {
        super(SapTransactionalIDocListServerEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V2CamelSapBindingModel createTestModel() {
        V2IDocServerModel endpoint = new V2IDocServerModel(CamelSapNamespace.V_2_0.uri(), Constants.IDOCLIST_SERVER);
        endpoint.setServerName(SERVER_NAME);
        endpoint.setIdocType(IDOC_TYPE);
        endpoint.setIdocTypeExtension(IDOC_TYPE_EXTENSION);
        endpoint.setSystemRelease(SYSTEM_RELEASE);
        endpoint.setApplicationRelease(APPLICATION_RELEASE);
        return new V2CamelSapBindingModel(CamelSapNamespace.V_2_0.uri()).setEndpointModel(endpoint);
    }

    @Override
    protected void createModelAssertions(V2CamelSapBindingModel model) {
        EndpointModel endpoint = model.getEndpointModel();
        assertEquals(ENDPOINT_TYPE, endpoint.getName());
        assertEquals(V2IDocServerModel.class, endpoint.getClass());
        V2IDocServerModel idocServer = V2IDocServerModel.class.cast(endpoint);
        assertEquals(SERVER_NAME, idocServer.getServerName());
        assertEquals(IDOC_TYPE, idocServer.getIdocType());
        assertEquals(IDOC_TYPE_EXTENSION, idocServer.getIdocTypeExtension());
        assertEquals(SYSTEM_RELEASE, idocServer.getSystemRelease());
        assertEquals(APPLICATION_RELEASE, idocServer.getApplicationRelease());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
