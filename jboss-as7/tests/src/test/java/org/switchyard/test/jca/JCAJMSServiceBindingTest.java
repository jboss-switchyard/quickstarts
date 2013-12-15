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

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
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
 * Functional tests for JCA service binding.
 */
@RunWith(Arquillian.class)
public class JCAJMSServiceBindingTest  {

    private static final String TEST_CONFIG = "org/switchyard/test/jca/switchyard-inbound-jms-test.xml";
    private static final String INPUT_QUEUE = "TestQueue";
    private static final String RESULT_QUEUE = "StoreResultQueue";
    private static final String FAULT_QUEUE = "StoreFaultQueue";

    private Logger _logger = Logger.getLogger(JCAJMSServiceBindingTest.class);
    private HornetQMixIn _hqMixIn;

    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws Exception {
        ResourceDeployer.addQueue(INPUT_QUEUE);
        ResourceDeployer.addQueue(RESULT_QUEUE);
        ResourceDeployer.addQueue(FAULT_QUEUE);
        ResourceDeployer.addPropertiesUser();

        URL testConfigUrl = Classes.getResource(TEST_CONFIG);
        String beansXml = Descriptors.create(BeansDescriptor.class).exportAsString();
        return ShrinkWrap.create(JavaArchive.class, "switchyard-JCAJMSServiceBindingTest.jar")
                         .addClass(JCAJMSService.class)
                         .addClass(JCAJMSServiceImpl.class)
                         .addClass(JCAJMSFault.class)
                         .addClass(MyJMSContextMapper.class)
                         .addClass(MyJMSMessageComposer.class)
                         .addAsManifestResource(new UrlAsset(testConfigUrl), "switchyard.xml")
                         .addAsManifestResource(new StringAsset(beansXml), "beans.xml");
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
            ResourceDeployer.removeQueue(INPUT_QUEUE);
            ResourceDeployer.removeQueue(RESULT_QUEUE);
            ResourceDeployer.removeQueue(FAULT_QUEUE);
        } catch (Exception e) {
            _logger.warn(e);
        }
        
    }

    @Test
    public void testInflowJMS() throws Exception {
        Session session = _hqMixIn.createJMSSession();
        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText("onMessage");
            producer.send(inMsg);
            producer.close();
            session.close();
            
            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(RESULT_QUEUE));
            assertMessage(consumer.receive(1000), TextMessage.class, "onMessagetesttest");
            Assert.assertNull(consumer.receive(1000));
            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(FAULT_QUEUE));
            Assert.assertNull(consumer.receive(1000));
            consumer.close();
        } finally {
            session.close();
        }
    }
    
    @Test
    public void testInflowJMS_fault_rollback() throws Exception {
        Session session = _hqMixIn.createJMSSession();
        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText("onMessage_fault_rollback");
            producer.send(inMsg);
            producer.close();
            session.close();

            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(FAULT_QUEUE));
            assertMessage(consumer.receive(1000), TextMessage.class, "faultmessagetest");
            assertMessage(consumer.receive(1000), TextMessage.class, "faultmessagetest");
            assertMessage(consumer.receive(1000), TextMessage.class, "faultmessagetest");
            assertMessage(consumer.receive(1000), TextMessage.class, "faultmessagetest");
            Assert.assertNull(consumer.receive(1000));
            consumer.close();
            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(RESULT_QUEUE));
            assertMessage(consumer.receive(1000), TextMessage.class, "onMessage_fault_rollbacktesttest");
            Assert.assertNull(consumer.receive(1000));
            consumer.close();
        } finally {
            session.close();
        }
    }

    @Test
    public void testInflowJMS_fault_commit() throws Exception {
        Session session = _hqMixIn.createJMSSession();
        try {
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(INPUT_QUEUE));
            TextMessage inMsg = session.createTextMessage();
            inMsg.setText("onMessage_fault_commit");
            producer.send(inMsg);
            producer.close();
            session.close();

            session = _hqMixIn.createJMSSession();
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(FAULT_QUEUE));
            assertMessage(consumer.receive(1000), TextMessage.class, "faultmessagetest");
            Assert.assertNull(consumer.receive(1000));
            consumer.close();
            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(RESULT_QUEUE));
            assertMessage(consumer.receive(1000), TextMessage.class, "onMessage_fault_committesttest");
            Assert.assertNull(consumer.receive(1000));
            consumer.close();
        } finally {
            session.close();
        }
    }

    private <T extends Message> void assertMessage(Message msg, Class<T> type, String expect) throws Exception {
        Assert.assertNotNull(msg);
        Assert.assertTrue(type.isAssignableFrom(msg.getClass()));
        Assert.assertEquals(expect, TextMessage.class.cast(msg).getText());
        
    }
}

