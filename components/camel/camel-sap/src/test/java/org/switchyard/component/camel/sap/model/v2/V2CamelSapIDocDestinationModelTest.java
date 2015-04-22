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

import org.fusesource.camel.component.sap.SapTransactionalIDocDestinationEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.sap.model.CamelSapNamespace;
import org.switchyard.component.camel.sap.model.EndpointModel;

/**
 * Test for {@link V2CamelSapIDocDestinationModel}.
 */
public class V2CamelSapIDocDestinationModelTest extends V1BaseCamelReferenceBindingModelTest<V2CamelSapBindingModel, SapTransactionalIDocDestinationEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-sap-idoc-destination-beans.xml";
    private static final String ENDPOINT_TYPE = Constants.IDOC_DESTINATION;
    private static final String DESTINATION_NAME = "nplHost";
    private static final String IDOC_TYPE = "BAPI_FLTRIP_GETLIST";
    private static final String IDOC_TYPE_EXTENSION = "IDOC_TYPE_EXTENSION";
    private static final String SYSTEM_RELEASE = "SYSTEM_RELEASE";
    private static final String APPLICATION_RELEASE = "APPLICATION_RELEASE";
    private static final String CAMEL_URI = "sap-" + ENDPOINT_TYPE + ":" + DESTINATION_NAME + ":" + IDOC_TYPE
                                + ":" + IDOC_TYPE_EXTENSION + ":" + SYSTEM_RELEASE + ":" + APPLICATION_RELEASE;

    public V2CamelSapIDocDestinationModelTest() {
        super(SapTransactionalIDocDestinationEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V2CamelSapBindingModel createTestModel() {
        V2CamelSapBindingModel binding =
                new V2CamelSapBindingModel(CamelSapNamespace.V_2_0.uri()) {
                    @Override
                    public boolean isReferenceBinding() {
                        return true;
                    }
        };
        V2IDocDestinationModel endpoint = new V2IDocDestinationModel(CamelSapNamespace.V_2_0.uri(),
                                                Constants.IDOC_DESTINATION);
        endpoint.setDestinationName(DESTINATION_NAME);
        endpoint.setIdocType(IDOC_TYPE);
        endpoint.setIdocTypeExtension(IDOC_TYPE_EXTENSION);
        endpoint.setSystemRelease(SYSTEM_RELEASE);
        endpoint.setApplicationRelease(APPLICATION_RELEASE);
        return binding.setEndpointModel(endpoint);
    }

    @Override
    protected void createModelAssertions(V2CamelSapBindingModel model) {
        EndpointModel endpoint = model.getEndpointModel();
        assertEquals(ENDPOINT_TYPE, endpoint.getName());
        assertEquals(V2IDocDestinationModel.class, endpoint.getClass());
        V2IDocDestinationModel idocDest = V2IDocDestinationModel.class.cast(endpoint);
        assertEquals(DESTINATION_NAME, idocDest.getDestinationName());
        assertEquals(IDOC_TYPE, idocDest.getIdocType());
        assertEquals(IDOC_TYPE_EXTENSION, idocDest.getIdocTypeExtension());
        assertEquals(SYSTEM_RELEASE, idocDest.getSystemRelease());
        assertEquals(APPLICATION_RELEASE, idocDest.getApplicationRelease());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
