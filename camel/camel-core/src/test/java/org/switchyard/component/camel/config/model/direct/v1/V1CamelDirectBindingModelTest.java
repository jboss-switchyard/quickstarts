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
package org.switchyard.component.camel.config.model.direct.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.direct.DirectEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.core.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelDirectBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelDirectBindingModel, DirectEndpoint> {

    private static final String CAMEL_XML = "switchyard-direct-binding-beans.xml";

    private static final String NAME = "fooDirectName";

    private static final String CAMEL_URI = "direct://fooDirectName";

    public V1CamelDirectBindingModelTest() {
        super(DirectEndpoint.class, CAMEL_XML);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

    @Override
    protected void createModelAssertions(V1CamelDirectBindingModel model) {
        assertEquals(NAME, model.getEndpointName());
    }

    @Override
    protected V1CamelDirectBindingModel createTestModel() {
        return new V1CamelDirectBindingModel().setEndpointName(NAME);
    }

}