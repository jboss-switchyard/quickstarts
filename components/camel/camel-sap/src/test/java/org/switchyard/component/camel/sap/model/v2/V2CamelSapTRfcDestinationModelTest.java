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

import org.fusesource.camel.component.sap.SapSynchronousRfcDestinationEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.sap.model.CamelSapNamespace;
import org.switchyard.component.camel.sap.model.EndpointModel;

/**
 * Test for {@link V2CamelSapSRfcDestinationModel}.
 */
public class V2CamelSapTRfcDestinationModelTest extends V1BaseCamelReferenceBindingModelTest<V2CamelSapBindingModel, SapSynchronousRfcDestinationEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-sap-srfc-destination-beans.xml";
    private static final String ENDPOINT_TYPE = Constants.SRFC_DESTINATION;
    private static final String DESTINATION_NAME = "nplHost";
    private static final String RFC_NAME = "BAPI_FLTRIP_GETLIST";
    private static final Boolean TRANSACTED = Boolean.TRUE;
    private static final String CAMEL_URI = "sap-" + ENDPOINT_TYPE + ":" + DESTINATION_NAME + ":" + RFC_NAME
                                + "?transacted=" + TRANSACTED;

    public V2CamelSapTRfcDestinationModelTest() {
        super(SapSynchronousRfcDestinationEndpoint.class, CAMEL_XML);

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
        V2RfcDestinationModel endpoint = new V2RfcDestinationModel(CamelSapNamespace.V_2_0.uri(),
                                                Constants.SRFC_DESTINATION);
        endpoint.setDestinationName(DESTINATION_NAME);
        endpoint.setRfcName(RFC_NAME);
        endpoint.setTransacted(TRANSACTED);
        return binding.setEndpointModel(endpoint);
    }

    @Override
    protected void createModelAssertions(V2CamelSapBindingModel model) {
        EndpointModel endpoint = model.getEndpointModel();
        assertEquals(ENDPOINT_TYPE, endpoint.getName());
        assertEquals(V2RfcDestinationModel.class, endpoint.getClass());
        V2RfcDestinationModel rfcDest = V2RfcDestinationModel.class.cast(endpoint);
        assertEquals(DESTINATION_NAME, rfcDest.getDestinationName());
        assertEquals(RFC_NAME, rfcDest.getRfcName());
        assertEquals(TRANSACTED, rfcDest.isTransacted());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
