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
package org.switchyard.component.camel.config.model.jms.v1;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.jms.CamelJmsBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test for {@link V1CamelJmsBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJmsBindingModelTest extends V1BaseCamelModelTest<V1CamelJmsBindingModel> {

    private static final String CAMEL_XML = "switchyard-jms-binding-beans.xml";
    private static final String CAMEL_INVALID_XML = "switchyard-invalid-jms-binding-beans.xml";

    private static final String QUEUE = "test_queue";
    private static String TOPIC = "esb_in_topic";
    private static String CONNECTION_FACTORY = "connFactory";
    private static String USERNAME = "camel";
    private static String PASSWORD = "isMyFriend";
    private static String CLIENT_ID = "esb_in";
    private static String DURABLE_SUBSCRIPTION_NAME = "esb_in_sub";
    private static Integer CONCURRENT_CONSUMERS = 5;
    private static Integer MAX_CONCURRENT_CONSUMERS = 15;
    private static Boolean DISABLE_REPLY_TO = true;
    private static Boolean PRESERVE_MESSAGE_QOS = true;
    private static Boolean DELIVERY_PERSISTENT = false;
    private static Integer PRIORITY = 9;
    private static Boolean EXPLICIT_QOS_ENABLED = true;
    private static String REPLY_TO = "esb_out";
    private static String REPLY_TO_TYPE= "Shared";
    private static Integer REQUEST_TIMEOUT = 300;
    private static String SELECTOR = "DEST='ESB'";
    private static Integer TIME_TO_LIVE = 3600;
    private static Boolean TRANSACTED = true;

    private static final String COMPONENT_URI = "jms:topic:esb_in_topic?connectionFactory=connFactory&" +
        "username=camel&password=isMyFriend&clientId=esb_in&durableSubscriptionName=esb_in_sub&" +
        "concurrentConsumers=5&maxConcurrentConsumers=15&disableReplyTo=true&preserveMessageQos=true&" +
        "deliveryPersistent=false&priority=9&explicitQosEnabled=true&replyTo=esb_out&replyToType=Shared&" +
        "requestTimeout=300&selector=DEST='ESB'&timeToLive=3600&transacted=true";

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelJmsBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.isValid());
        assertModel(bindingModel);
        assertEquals(COMPONENT_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void verifyQueueOrTopic() {
        V1CamelJmsBindingModel model = new V1CamelJmsBindingModel();
        model.setConnectionFactory(CONNECTION_FACTORY);
        model.setTopic(TOPIC);
        model.setQueue(QUEUE);

        String uri = model.getComponentURI().toString();
        assertTrue(uri.startsWith("jms:queue"));
        assertFalse(model.validateModel().isValid());
    }

    @Test
    public void verifyNoDestination() {
        V1CamelJmsBindingModel model = new V1CamelJmsBindingModel();
        model.setConnectionFactory(CONNECTION_FACTORY);

        assertFalse(model.validateModel().isValid());
    }

    @Test
    public void invalidConfig() throws Exception {
        V1CamelJmsBindingModel binding = getFirstCamelBinding(CAMEL_INVALID_XML);
        assertFalse(binding.validateModel().isValid());
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    private V1CamelJmsBindingModel createModel() {
        return (V1CamelJmsBindingModel) new V1CamelJmsBindingModel()
            .setTopic(TOPIC)
            .setConnectionFactory(CONNECTION_FACTORY)
            .setUsername(USERNAME)
            .setPassword(PASSWORD)
            .setClientId(CLIENT_ID)
            .setDurableSubscriptionName(DURABLE_SUBSCRIPTION_NAME)
            .setConcurrentConsumers(CONCURRENT_CONSUMERS)
            .setMaxConcurrentConsumers(MAX_CONCURRENT_CONSUMERS)
            .setDisableReplyTo(DISABLE_REPLY_TO)
            .setPreserveMessageQos(PRESERVE_MESSAGE_QOS)
            .setDeliveryPersistent(DELIVERY_PERSISTENT)
            .setPriority(PRIORITY)
            .setExplicitQosEnabled(EXPLICIT_QOS_ENABLED)
            .setReplyTo(REPLY_TO)
            .setReplyToType(REPLY_TO_TYPE)
            .setRequestTimeout(REQUEST_TIMEOUT)
            .setSelector(SELECTOR)
            .setTimeToLive(TIME_TO_LIVE)
            .setTransacted(TRANSACTED);
    }

    private void assertModel(CamelJmsBindingModel model) {
        assertEquals(TOPIC, model.getTopic());
        assertEquals(CONNECTION_FACTORY, model.getConnectionFactory());
        assertEquals(USERNAME, model.getUsername());
        assertEquals(PASSWORD, model.getPassword());
        assertEquals(CLIENT_ID, model.getClientId());
        assertEquals(DURABLE_SUBSCRIPTION_NAME, model.getDurableSubscriptionName());
        assertEquals(CONCURRENT_CONSUMERS, model.getConcurrentConsumers());
        assertEquals(MAX_CONCURRENT_CONSUMERS, model.getMaxConcurrentConsumers());
        assertEquals(DISABLE_REPLY_TO, model.isDisableReplyTo());
        assertEquals(PRESERVE_MESSAGE_QOS, model.isPreserveMessageQos());
        assertEquals(DELIVERY_PERSISTENT, model.isDeliveryPersistent());
        assertEquals(PRIORITY, model.getPriority());
        assertEquals(EXPLICIT_QOS_ENABLED, model.isExplicitQosEnabled());
        assertEquals(REPLY_TO, model.getReplyTo());
        assertEquals(REPLY_TO_TYPE, model.getReplyToType());
        assertEquals(REQUEST_TIMEOUT, model.getRequestTimeout());
        assertEquals(SELECTOR, model.getSelector());
        assertEquals(TIME_TO_LIVE, model.getTimeToLive());
        assertEquals(TRANSACTED, model.isTransacted());
    }
}
