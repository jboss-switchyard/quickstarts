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
