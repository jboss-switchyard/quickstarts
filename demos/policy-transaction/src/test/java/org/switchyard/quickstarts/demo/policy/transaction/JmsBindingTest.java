/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.demo.policy.transaction;

import java.io.File;
import java.util.zip.ZipFile;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.testutil.JBossCliUtil;

/**
 * Functional test for a policy-transaction demo quickstart.
 */
@RunWith(Arquillian.class)
public class JmsBindingTest {
    private static final Logger _logger = Logger.getLogger(JmsBindingTest.class);
    private static final String CLI_CONFIG_FILE = System.getProperty("cli.config.file");
    private static final String CLI_UNCONFIG_FILE = System.getProperty("cli.unconfig.file");
    private static final String HORNETQ_USER = System.getProperty("hornetq.user");
    private static final String HORNETQ_PASSWORD = System.getProperty("hornetq.password");
    private static final String QUICKSTART_JAR = System.getProperty("quickstart.jar");

    @Resource(mappedName = "/ConnectionFactory")
    private ConnectionFactory _connectionFactory;

    @Resource(mappedName = "policyQSTransacted")
    private Destination _queueIn;

    @Resource(mappedName = "policyQSNonTransacted")
    private Destination _queueInNoTx;

    @Resource(mappedName = "queueA")
    private Destination _queueOutA;

    @Resource(mappedName = "queueB")
    private Destination _queueOutB;

    @Resource(mappedName = "queueC")
    private Destination _queueOutC;

    @Deployment
    public static Archive<?> createTestArchive() {
        File artifact = new File(QUICKSTART_JAR);
        if (!artifact.exists()) {
            String error = QUICKSTART_JAR + " not found. Do \"mvn package\" before the test";
            _logger.error(error);
            throw new RuntimeException(error);
        }

        try {
            JBossCliUtil.executeCliScript(CLI_CONFIG_FILE);
            JavaArchive archive = ShrinkWrap.create(ZipImporter.class, artifact.getName())
                    .importFrom(new ZipFile(artifact))
                    .as(JavaArchive.class);
                    //.addAsManifestResource(new File(QUEUE_FILE));
            // SWITCHYARD-2418 replace manifest file to remove OSGi related attributes
            archive.delete("META-INF/MANIFEST.MF");
            archive.addManifest();
            return archive;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        JBossCliUtil.executeCliScript(CLI_UNCONFIG_FILE);
    }

    /**
     * Triggers the 'WorkService' by sending a HornetQ Message to the 'policyQSTransacted' queue.
     */
    @Test
    public void testRollbackA() throws Exception {
        String command = "rollback.A";
        Connection conn = _connectionFactory.createConnection(HORNETQ_USER, HORNETQ_PASSWORD);
        conn.start();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(_queueIn);
            TextMessage message = session.createTextMessage();
            message.setText(command);
            producer.send(message);
            session.close();

            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(_queueOutA);
            TextMessage msg = TextMessage.class.cast(consumer.receive(30000));
            Assert.assertEquals(command, msg.getText());
            Assert.assertNull(consumer.receive(1000));
            consumer.close();

            consumer = session.createConsumer(_queueOutB);
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            Assert.assertNull(consumer.receive(1000));
            consumer.close();

            consumer = session.createConsumer(_queueOutC);
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            Assert.assertNull(consumer.receive(1000));
            session.close();
        } finally {
            conn.close();
        }
    }

    @Test
    public void testRollbackB() throws Exception {
        String command = "rollback.B";
        Connection conn = _connectionFactory.createConnection(HORNETQ_USER, HORNETQ_PASSWORD);
        conn.start();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(_queueIn);
            TextMessage message = session.createTextMessage();
            message.setText(command);
            producer.send(message);
            session.close();

            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(_queueOutA);
            TextMessage msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            Assert.assertNull(consumer.receive(1000));
            consumer.close();

            consumer = session.createConsumer(_queueOutB);
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            Assert.assertNull(consumer.receive(1000));
            consumer.close();

            consumer = session.createConsumer(_queueOutC);
            msg = TextMessage.class.cast(consumer.receive(1000));
            Assert.assertEquals(command, msg.getText());
            Assert.assertNull(consumer.receive(1000));
            session.close();
        } finally {
            conn.close();
        }
    }

    @Test
    public void testNonTransacted() throws Exception {
        String command = "rollback.A";
        Connection conn = _connectionFactory.createConnection(HORNETQ_USER, HORNETQ_PASSWORD);
        conn.start();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(_queueInNoTx);
            TextMessage message = session.createTextMessage();
            message.setText(command);
            producer.send(message);
            session.close();

            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(_queueOutA);
            Assert.assertNull(consumer.receive(1000));
            consumer = session.createConsumer(_queueOutB);
            Assert.assertNull(consumer.receive(1000));
            consumer = session.createConsumer(_queueOutC);
            Assert.assertNull(consumer.receive(1000));
        } finally {
            conn.close();
        }
    }

}
