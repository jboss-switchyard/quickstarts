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
package org.switchyard.component.camel.config.model.v1;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.apache.camel.component.direct.DirectEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelReferenceBindingModelTest extends V1BaseCamelReferenceBindingModelTest<V1CamelBindingModel, DirectEndpoint> {

    private final static String CAMEL_XML = "switchyard-camel-ref-beans.xml";
    private final static String CAMEL_URI = "direct://input";

    public V1CamelReferenceBindingModelTest() {
        super(DirectEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelBindingModel createTestModel() {
        return new V1CamelBindingModel()
            .setConfigURI(URI.create("direct://input"));
    }

    @Override
    protected void createModelAssertions(V1CamelBindingModel model) {
        assertEquals("uri", model.getType());
        assertEquals("direct", model.getConfigURI().getScheme());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
