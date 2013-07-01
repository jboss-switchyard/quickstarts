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
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.jpa.model.CamelJpaProducerBindingModel;

/**
 * Test for {@link V1CamelJpaProducerBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaProducerBindingModelTest extends V1BaseCamelReferenceBindingModelTest<V1CamelJpaBindingModel, JpaEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-jpa-binding-producer-beans.xml";

    private static final String CAMEL_URI = "jpa://some.clazz.Name?persistenceUnit=MyPU&flushOnSend=false&usePersist=false";

    private static final Boolean FLUSH_ON_SEND = false;
    private static final Boolean USE_PERSIST = false;

    public V1CamelJpaProducerBindingModelTest() {
        super(JpaEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelJpaBindingModel createTestModel() {
        V1CamelJpaBindingModel model = new V1CamelJpaBindingModel();
        model.setEntityClassName(V1CamelJpaBindingModelTest.ENTITY_CLASS_NAME);
        model.setPersistenceUnit(V1CamelJpaBindingModelTest.PERSISTENCE_UNIT);

        CamelJpaProducerBindingModel producer = new V1CamelJpaProducerBindingModel()
            .setFlushOnSend(FLUSH_ON_SEND)
            .setUsePersist(USE_PERSIST);
        return model.setProducer(producer);
    }

    @Override
    protected void createModelAssertions(V1CamelJpaBindingModel model) {
        CamelJpaProducerBindingModel producer = model.getProducer();
        assertEquals(FLUSH_ON_SEND, producer.isFlushOnSend());
        assertEquals(USE_PERSIST, producer.isUsePersist());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
