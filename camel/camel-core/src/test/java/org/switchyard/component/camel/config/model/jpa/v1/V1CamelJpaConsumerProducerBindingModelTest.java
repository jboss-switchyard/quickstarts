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
package org.switchyard.component.camel.config.model.jpa.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.apache.camel.component.jpa.JpaEndpoint;
import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.jpa.CamelJpaBindingModel;
import org.switchyard.component.camel.config.model.jpa.CamelJpaConsumerBindingModel;
import org.switchyard.component.camel.config.model.jpa.CamelJpaProducerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelJpaConsumerBindingModel} and {@link V1CamelJpaProducerBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaConsumerProducerBindingModelTest extends V1BaseCamelModelTest<V1CamelJpaBindingModel> {

    private static final String CAMEL_XML = "switchyard-jpa-binding-consumer-producer-beans.xml";

    private static final String CAMEL_URI = UnsafeUriCharactersEncoder.encode(
        "jpa://some.clazz.Name?persistenceUnit=MyPU&" +
        "consumeDelete=true&consumeLockEntity=true&maximumResults=10&consumer.query=from User&" +
        "consumer.namedQuery=fetchUsers&consumer.nativeQuery=select * from users&" +
        "consumer.resultClass=java.util.ArrayList&consumer.transacted=true&" +
        "flushOnSend=false&usePersist=false");

    private static final Boolean CONSUME_DELETE = true;
    private static final Boolean CONSUME_LOCK_ENTITY = true;
    private static final Integer MAXIMUM_RESULTS = 10;
    private static final String NAMED_QUERY = "fetchUsers";
    private static final String QUERY = "from User";
    private static final String NATIVE_QUERY = "select * from users";
    private static final String RESULT_CLASS = "java.util.ArrayList";
    private static final Boolean TRANSACTED = true;
    private static final Boolean FLUSH_ON_SEND = false;
    private static final Boolean USE_PERSIST = false;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConfigOverride() {
        // set a value on an existing config element
        CamelJpaBindingModel bindingModel = createJpaModel();
        assertEquals(TRANSACTED, bindingModel.getConsumer().isTransacted());
        bindingModel.getConsumer().setTransacted(false);
        assertFalse(bindingModel.getConsumer().isTransacted());
    }

    @Test
    public void testReadConfig() throws Exception {
        final V1CamelJpaBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.isValid());

        assertModel(bindingModel.getConsumer());
        assertModel(bindingModel.getProducer());
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void testWriteConfig() throws Exception {
        CamelJpaBindingModel bindingModel = createJpaModel();
        assertModel(bindingModel.getConsumer());
        assertModel(bindingModel.getProducer());
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    /**
     * This test fails because of namespace prefix
     * 
     */
    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createJpaModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testComponentURI() {
        CamelJpaBindingModel bindingModel = createJpaModel();
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void testCamelEndpoint() {
        CamelJpaBindingModel model = createJpaModel();
        JpaEndpoint endpoint = getEndpoint(model, JpaEndpoint.class);
        assertEquals(MAXIMUM_RESULTS.intValue(), endpoint.getMaximumResults());
    }

    private CamelJpaBindingModel createJpaModel() {
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
        model.setConsumer(consumer);
        CamelJpaProducerBindingModel producer = new V1CamelJpaProducerBindingModel()
            .setFlushOnSend(FLUSH_ON_SEND)
            .setUsePersist(USE_PERSIST);
        model.setProducer(producer);

        return model;
    }

    private void assertModel(CamelJpaConsumerBindingModel consumer) {
        assertEquals(CONSUME_DELETE, consumer.isConsumeDelete());
        assertEquals(CONSUME_LOCK_ENTITY, consumer.isConsumeLockEntity());
        assertEquals(MAXIMUM_RESULTS, consumer.getMaximumResults());
        assertEquals(QUERY, consumer.getQuery());
        assertEquals(NAMED_QUERY, consumer.getNamedQuery());
        assertEquals(RESULT_CLASS, consumer.getResultClass());
        assertEquals(TRANSACTED, consumer.isTransacted());
    }

    private void assertModel(CamelJpaProducerBindingModel producer) {
        assertEquals(FLUSH_ON_SEND, producer.isFlushOnSend());
        assertEquals(USE_PERSIST, producer.isUsePersist());
    }
}
