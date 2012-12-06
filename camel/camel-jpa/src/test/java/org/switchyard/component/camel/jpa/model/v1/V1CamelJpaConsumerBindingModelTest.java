/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.jpa.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.jpa.JpaEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.jpa.model.CamelJpaConsumerBindingModel;

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
        V1CamelJpaBindingModel model = new V1CamelJpaBindingModel();
        model.setEntityClassName(V1CamelJpaBindingModelTest.ENTITY_CLASS_NAME);
        model.setPersistenceUnit(V1CamelJpaBindingModelTest.PERSISTENCE_UNIT);

        CamelJpaConsumerBindingModel consumer = (CamelJpaConsumerBindingModel) new V1CamelJpaConsumerBindingModel()
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
