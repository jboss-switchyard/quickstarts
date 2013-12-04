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
package org.switchyard.quickstarts.jca.inflow;

import java.io.File;
import java.util.zip.ZipFile;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.usage.SystemUsage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Functional test for a switchyard-quickstart-jca-inflow-activemq.
 */
@RunWith(Arquillian.class)
public class JCAInflowBindingTest {
    private static final String JAR_FILE = "target/switchyard-quickstart-jca-inflow-activemq.jar";
    private static final String QUEUE = "JCAInflowGreetingServiceQueue";
    private static final File ACTIVEMQ_DATA_DIR = new File(System.getProperty("java.io.tmpdir"), "activemq-data");
    private static final long MEMORY_STORE_USAGE_LIMIT = 209715200;
    private static final long TEMP_STORE_USAGE_LIMIT = 209715200;

    private static Logger _logger = Logger.getLogger(JCAInflowBindingTest.class);

    private static BrokerService _broker;

    public static void startActiveMQBroker() throws Exception {
        _broker = new BrokerService();
        _broker.setBrokerName("default");
        _broker.setUseJmx(false);
        _broker.setPersistent(false);
        _broker.setDataDirectoryFile(ACTIVEMQ_DATA_DIR);
        _broker.addConnector(ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL);
        SystemUsage systemUsage = _broker.getSystemUsage();
        systemUsage.getMemoryUsage().setLimit(MEMORY_STORE_USAGE_LIMIT);
        systemUsage.getTempUsage().setLimit(TEMP_STORE_USAGE_LIMIT);
        _broker.start();
    }

    @Deployment
    public static Archive<?> createTestArchive() throws Exception {
        startActiveMQBroker();

        File artifact = new File(JAR_FILE);
        try {
            return ShrinkWrap.create(ZipImporter.class, artifact.getName())
                .importFrom(new ZipFile(artifact))
                .as(JavaArchive.class);
        } catch (Exception e) {
            throw new RuntimeException(JAR_FILE + " not found. Do \"mvn package\" before the test", e);
        }
    }

    /**
     * Triggers the 'GreetingService' by sending a ActiveMQ Message
     */
    @Test
    public void triggerGreetingService() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection(ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(QUEUE);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage();
            message.setText(PAYLOAD);
            producer.send(message);
            session.close();
            _logger.info("Sent a message into " + QUEUE + " - following message should appear on server console:");
            _logger.info("Hello there dummy :-)");

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(queue);
            Assert.assertNull("Request message is still in the queue: " + QUEUE, consumer.receive(3000));
            session.close();
        } finally {
            if (connection != null) {
                connection.close();
            }

        }
    }

    private static final String PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<person xmlns=\"urn:switchyard-quickstart:jca-inflow-activemq:0.1.0\">\n" + "    <name>dummy</name>\n"
        + "    <language>english</language>\n" + "</person>\n";

}
