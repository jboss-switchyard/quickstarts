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

import org.fusesource.camel.component.sap.SapQueuedIDocListDestinationEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.sap.model.CamelSapNamespace;
import org.switchyard.component.camel.sap.model.EndpointModel;

/**
 * Test for {@link V2CamelSapQIDocListDestinationModel}.
 */
public class V2CamelSapQIDocListDestinationModelTest extends V1BaseCamelReferenceBindingModelTest<V2CamelSapBindingModel, SapQueuedIDocListDestinationEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-sap-qidoclist-destination-beans.xml";
    private static final String ENDPOINT_TYPE = Constants.QIDOCLIST_DESTINATION;
    private static final String DESTINATION_NAME = "nplHost";
    private static final String QUEUE_NAME = "QUEUE_NAME";
    private static final String IDOC_TYPE = "BAPI_FLTRIP_GETLIST";
    private static final String CAMEL_URI = "sap-" + ENDPOINT_TYPE + ":" + DESTINATION_NAME
            + ":" + QUEUE_NAME + ":" + IDOC_TYPE;

    public V2CamelSapQIDocListDestinationModelTest() {
        super(SapQueuedIDocListDestinationEndpoint.class, CAMEL_XML);

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
        V2QIDocDestinationModel endpoint = new V2QIDocDestinationModel(CamelSapNamespace.V_2_0.uri(),
                                                Constants.QIDOCLIST_DESTINATION);
        endpoint.setDestinationName(DESTINATION_NAME);
        endpoint.setQueueName(QUEUE_NAME);
        endpoint.setIdocType(IDOC_TYPE);
        return binding.setEndpointModel(endpoint);
    }

    @Override
    protected void createModelAssertions(V2CamelSapBindingModel model) {
        EndpointModel endpoint = model.getEndpointModel();
        assertEquals(ENDPOINT_TYPE, endpoint.getName());
        assertEquals(V2QIDocDestinationModel.class, endpoint.getClass());
        V2QIDocDestinationModel qidocDest = V2QIDocDestinationModel.class.cast(endpoint);
        assertEquals(DESTINATION_NAME, qidocDest.getDestinationName());
        assertEquals(QUEUE_NAME, qidocDest.getQueueName());
        assertEquals(IDOC_TYPE, qidocDest.getIdocType());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
