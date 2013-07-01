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
package org.switchyard.component.camel.jpa.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.jpa.JpaEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;

/**
 * Test for {@link V1CamelJpaBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelJpaBindingModel, JpaEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-jpa-binding-beans.xml";
    private static final String CAMEL_URI = "jpa://some.clazz.Name?persistenceUnit=MyPU";
    static final String ENTITY_CLASS_NAME = "some.clazz.Name";
    static final String PERSISTENCE_UNIT = "MyPU";

    public V1CamelJpaBindingModelTest() {
        super(JpaEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelJpaBindingModel createTestModel() {
        return new V1CamelJpaBindingModel()
            .setEntityClassName(ENTITY_CLASS_NAME)
            .setPersistenceUnit(PERSISTENCE_UNIT);
    }

    @Override
    protected void createModelAssertions(V1CamelJpaBindingModel model) {
        assertEquals(ENTITY_CLASS_NAME, model.getEntityClassName());
        assertEquals(PERSISTENCE_UNIT, model.getPersistenceUnit());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
