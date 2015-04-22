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

import org.apache.camel.component.mock.MockEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.core.model.CamelCoreNamespace;
import org.switchyard.component.camel.core.model.v1.V1CamelUriBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelMockBindingModel;

/**
 * Test for {@link V1CamelUriBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelMockBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelMockBindingModel, MockEndpoint> {

    private static final String CAMEL_XML = "switchyard-mock-binding-beans.xml";

    private static final String NAME = "fooMockName";
    private static final Integer REPORT_GROUP = new Integer(999);

    private static final String CAMEL_URI = "mock://fooMockName?reportGroup=999";

    public V1CamelMockBindingModelTest() {
        super(MockEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelMockBindingModel createTestModel() {
        return new V1CamelMockBindingModel(CamelCoreNamespace.V_1_0.uri()).setEndpointName(NAME).setReportGroup(REPORT_GROUP);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

    @Override
    protected void createModelAssertions(V1CamelMockBindingModel model) {
        assertEquals(NAME, model.getEndpointName());
        assertEquals(REPORT_GROUP, model.getReportGroup());
    }

}