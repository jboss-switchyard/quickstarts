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
import org.switchyard.component.camel.jpa.model.CamelJpaConsumerBindingModel;
import org.switchyard.component.camel.jpa.model.CamelJpaNamespace;

/**
 * Test for {@link V1CamelJpaConsumerBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaConsumerBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelJpaBindingModel, JpaEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-jpa-binding-consumer-beans.xml";

    private static final String CAMEL_URI = "jpa://some.clazz.Name?persistenceUnit=MyPU&" +
        "consumeDelete=true&consumeLockEntity=true&maximumResults=10&consumer.query=from User&" +
        "consumer.namedQuery=fetchUsers&consumer.nativeQuery=select * from users&" +
        "consumer.resultClass=java.util.ArrayList&consumer.transacted=true";

    private static final Boolean CONSUME_DELETE = true;
    private static final Boolean CONSUME_LOCK_ENTITY = true;
    private static final Integer MAXIMUM_RESULTS = 10;
    private static final String NAMED_QUERY = "fetchUsers";
    private static final String QUERY = "from User";
    private static final String NATIVE_QUERY = "select * from users";
    private static final String RESULT_CLASS = "java.util.ArrayList";
    private static final Boolean TRANSACTED = true;

    public V1CamelJpaConsumerBindingModelTest() {
        super(JpaEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelJpaBindingModel createTestModel() {
        V1CamelJpaBindingModel model = new V1CamelJpaBindingModel(CamelJpaNamespace.V_1_0.uri());
        model.setEntityClassName(V1CamelJpaBindingModelTest.ENTITY_CLASS_NAME);
        model.setPersistenceUnit(V1CamelJpaBindingModelTest.PERSISTENCE_UNIT);

        CamelJpaConsumerBindingModel consumer = (CamelJpaConsumerBindingModel) new V1CamelJpaConsumerBindingModel(CamelJpaNamespace.V_1_0.uri())
            .setConsumeDelete(CONSUME_DELETE)
            .setConsumeLockEntity(CONSUME_LOCK_ENTITY)
            .setMaximumResults(MAXIMUM_RESULTS)
            .setQuery(QUERY)
            .setNamedQuery(NAMED_QUERY)
            .setNativeQuery(NATIVE_QUERY)
            .setResultClass(RESULT_CLASS)
            .setTransacted(TRANSACTED);
        return model.setConsumer(consumer);
    }

    @Override
    protected void createModelAssertions(V1CamelJpaBindingModel model) {
        CamelJpaConsumerBindingModel consumer = model.getConsumer();
        assertEquals(CONSUME_DELETE, consumer.isConsumeDelete());
        assertEquals(CONSUME_LOCK_ENTITY, consumer.isConsumeLockEntity());
        assertEquals(MAXIMUM_RESULTS, consumer.getMaximumResults());
        assertEquals(QUERY, consumer.getQuery());
        assertEquals(NAMED_QUERY, consumer.getNamedQuery());
        assertEquals(RESULT_CLASS, consumer.getResultClass());
        assertEquals(TRANSACTED, consumer.isTransacted());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
