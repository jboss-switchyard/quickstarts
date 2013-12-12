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

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Functional test for the switchyard-quickstart-jca-inflow-hornetq.
 */
@RunWith(Arquillian.class)
public class JCAInflowBindingTest {
    private static final String QUEUE_FILE = "target/test-classes/switchyard-quickstart-jca-inflow-hornetq-jms.xml";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp.1";
    private static final String JAR_FILE = "target/switchyard-quickstart-jca-inflow-hornetq.jar";

    private static Logger _logger = Logger.getLogger(JCAInflowBindingTest.class);

    @Resource(mappedName = "/ConnectionFactory")
    private ConnectionFactory _connectionFactory;

    @Resource(mappedName = "JCAInflowGreetingServiceQueue")
    private Destination _destination;

    @Deployment
    public static Archive<?> createTestArchive() {
        File artifact = new File(JAR_FILE);
        try {
            return ShrinkWrap.create(ZipImporter.class, artifact.getName())
                .importFrom(new ZipFile(artifact))
                .as(JavaArchive.class)
                .addAsManifestResource(new File(QUEUE_FILE));
        } catch (Exception e) {
            throw new RuntimeException(JAR_FILE + " not found. Do \"mvn package\" before the test", e);
        }
    }

    @Test
    public void triggerGreetingService() throws Exception {
        Connection conn = _connectionFactory.createConnection(USER, PASSWD);

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(_destination);
            TextMessage message = session.createTextMessage();
            message.setText(PAYLOAD);
            producer.send(message);
            session.close();
            _logger.info("Sent a message into " + _destination.toString() + " - following message should appear on server console:");
            _logger.info("Hello there dummy :-)");

            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(_destination);
            conn.start();
            Assert.assertNull("Request message is still in the queue: " + _destination.toString(), consumer.receive(3000));
        } finally {
            conn.close();
        }
    }

    private static final String PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<person xmlns=\"urn:switchyard-quickstart:jca-inflow-hornetq:0.1.0\">\n"
        + "    <name>dummy</name>\n"
        + "    <language>english</language>\n"
        + "</person>\n";

}
