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
package org.switchyard.test.jca;

import java.net.URL;

import javax.jms.BytesMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.type.Classes;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

/**
 * Functional test for JCA reference binding.
 */
@RunWith(Arquillian.class)
public class JCAJMSReferenceBindingTest  {

    private static final String TEST_CONFIG = "org/switchyard/test/jca/switchyard-outbound-jms-test.xml";
    private static final String CAMEL_ROUTE = "org/switchyard/test/jca/switchyard-outbound-jms-camel-test-route.xml";
    private static final String JNDI_PROPERTIES = "org/switchyard/test/jca/outbound-jms-jndi.properties";
    private static final String DEST_JNDI_PROPERTIES = "org/switchyard/test/jca/outbound-jms-destination-jndi.properties";
    private static final String INPUT_TX_QUEUE = "TestQueue";
    private static final String INPUT_NOTX_QUEUE = "NonTransactedTestQueue";
    private static final String OUTPUT_QUEUE = "ResultQueue";
    private static final String OUTPUT_PROP_QUEUE = "ResultPropQueue";

    private Logger _logger = Logger.getLogger(JCAJMSReferenceBindingTest.class);
    private HornetQMixIn _hqMixIn;
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws Exception {
        ResourceDeployer.addQueue(INPUT_TX_QUEUE);
        ResourceDeployer.addQueue(INPUT_NOTX_QUEUE);
        ResourceDeployer.addQueue(OUTPUT_QUEUE);
        ResourceDeployer.addQueue(OUTPUT_PROP_QUEUE);

        URL testConfigUrl = Classes.getResource(TEST_CONFIG);
        URL camelRouteUrl = Classes.getResource(CAMEL_ROUTE);
        URL jndiProperties = Classes.getResource(JNDI_PROPERTIES);
        URL destJndiProperties = Classes.getResource(DEST_JNDI_PROPERTIES);
        String beansXml = Descriptors.create(BeansDescriptor.class).exportAsString();
        return ShrinkWrap.create(JavaArchive.class, "switchyard-JCAJMSReferenceBindingTest.jar")
                         .addClass(JCAJMSTransactionService.class)
                         .addClass(JCAJMSTransactionServiceImpl.class)
                         .addClass(JCAJMSReference.class)
                         .addClass(JCAJMSReferenceText.class)
                         .addClass(JCAJMSReferenceService.class)
                         .addClass(JCAJMSReferenceServiceImpl.class)
                         .addClass(JCAJMSFault.class)
                         .addClass(MyJMSContextMapper.class)
                         .addClass(MyJMSMessageComposer.class)
                         .addAsManifestResource(new UrlAsset(testConfigUrl), "switchyard.xml")
                         .addAsManifestResource(new StringAsset(beansXml), "beans.xml")
                         .addAsResource(new UrlAsset(jndiProperties), "jndi.properties")
                         .addAsResource(new UrlAsset(destJndiProperties), "destination-jndi.properties")
                         .addAsResource(new UrlAsset(camelRouteUrl), "camel-route.xml");
    }

    @Before
    public void before() {
        _hqMixIn = new HornetQMixIn(false).setUser(ResourceDeployer.USER)
                                          .setPassword(ResourceDeployer.PASSWD);
        _hqMixIn.initialize();
    }

    @After
    public void after() {
        try {
            _hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(INPUT_TX_QUEUE);
            ResourceDeployer.removeQueue(INPUT_NOTX_QUEUE);
            ResourceDeployer.removeQueue(OUTPUT_QUEUE);
            ResourceDeployer.removeQueue(OUTPUT_PROP_QUEUE);
        } catch (Exception e) {
            _logger.warn(e);
        }
    }

    @Test
    public void testUnmanagedOutboundJMS() throws Exception {
        String payload = "onMessage";
        Session session = _hqMixIn.createJMSSession();

        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_NOTX_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText(payload);
            producer.send(inMsg);
            producer.close();
            session.close();

            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(OUTPUT_QUEUE));
            javax.jms.Message msg = consumer.receive(3000);
            consumer.close();
            Assert.assertNotNull(msg);
            Assert.assertTrue(msg instanceof ObjectMessage);
            Assert.assertEquals(payload+"test", ObjectMessage.class.cast(msg).getObject().toString());
            Assert.assertEquals("testVal", msg.getStringProperty("testProp"));
        } finally {
            session.close();
        }
    }
    
    @Test
    public void testUnmanagedOutboundJMSSpecifyingMessageType() throws Exception {
        String payload = "onMessageText";
        Session session = _hqMixIn.createJMSSession();

        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_NOTX_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText(payload);
            producer.send(inMsg);
            producer.close();
            session.close();

            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(OUTPUT_QUEUE));
            javax.jms.Message msg = consumer.receive(3000);
            consumer.close();
            Assert.assertNotNull(msg);
            Assert.assertTrue(msg instanceof TextMessage);
            Assert.assertEquals(payload+"test", TextMessage.class.cast(msg).getText());
            Assert.assertEquals("testVal", msg.getStringProperty("testProp"));
        } finally {
            session.close();
        }
    }

    @Test
    public void testManagedOutboundJMS() throws Exception {
        String payload = "onMessageManaged";
        Session session = _hqMixIn.createJMSSession();
        
        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_TX_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText(payload);
            producer.send(inMsg);
            producer.close();
            session.close();

            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(OUTPUT_QUEUE));
            javax.jms.Message msg = consumer.receive(3000);
            consumer.close();
            Assert.assertNotNull(msg);
            Assert.assertTrue(msg instanceof ObjectMessage);
            Assert.assertEquals(payload+"test", ObjectMessage.class.cast(msg).getObject().toString());
            Assert.assertEquals("testVal", msg.getStringProperty("testProp"));
        } finally {
            session.close();
        }
    }

    @Test
    public void testManagedOutboundJMSSetHeaderFromCamelRoute() throws Exception {
        String payload = "onMessageCamel";
        Session session = _hqMixIn.createJMSSession();

        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_TX_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText(payload);
            producer.send(inMsg);
            producer.close();
            session.close();

            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(OUTPUT_QUEUE));
            javax.jms.Message msg = consumer.receive(3000);
            consumer.close();
            Assert.assertNotNull(msg);
            Assert.assertTrue(msg instanceof ObjectMessage);
            Assert.assertEquals(payload, ObjectMessage.class.cast(msg).getObject().toString());
            Assert.assertEquals("jmscorrelation-onMessageCamel", msg.getJMSCorrelationID());
        } finally {
            session.close();
        }
    }

    @Test
    public void testManagedOutboundJMSWithContextProperty() throws Exception {
        String payload = "onMessageContextProperty";
        Session session = _hqMixIn.createJMSSession();

        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_TX_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText(payload);
            producer.send(inMsg);
            producer.close();
            session.close();

            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(OUTPUT_PROP_QUEUE));
            javax.jms.Message msg = consumer.receive(3000);
            consumer.close();
            Assert.assertNotNull(msg);
            Assert.assertTrue(msg instanceof BytesMessage);
            BytesMessage bmsg = BytesMessage.class.cast(msg);
            byte[] bytes = new byte[(int)bmsg.getBodyLength()];
            bmsg.readBytes(bytes);
            Assert.assertEquals(payload + "test", new String(bytes));
        } finally {
            session.close();
        }
    }
}

