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
package org.switchyard.quickstarts.jca.outbound;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipFile;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

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
 * Functional test for the switchyard-quickstart-jca-outbound-hornetq.
 */
@RunWith(Arquillian.class)
public class JCAOutboundBindingTest {
    private static final String QUEUE_FILE = "target/test-classes/switchyard-quickstart-jca-outbound-hornetq-jms.xml";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp.1";
    private static final String JAR_FILE = "target/switchyard-quickstart-jca-outbound-hornetq.jar";

    @Resource(mappedName = "/ConnectionFactory")
    private ConnectionFactory _connectionFactory;

    @Resource(mappedName = "OrderQueue")
    private Destination _orderQueue;

    @Resource(mappedName = "ShippingQueue")
    private Destination _shippingQueue;

    @Resource(mappedName = "FillingStockQueue")
    private Destination _fillingStockQueue;

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
    public void testOrderService() throws Exception {
        String[] orders = { "BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM" };
        Connection conn = _connectionFactory.createConnection(USER, PASSWD);
        conn.start();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(_orderQueue);
            for (String order : orders) {
                final TextMessage message = session.createTextMessage();
                message.setText(order);
                producer.send(message);
            }
            session.close();

            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(_orderQueue);
            Assert.assertNull("Request message is still in the queue:" + _orderQueue, consumer.receive(3000));
            consumer.close();

            consumer = session.createConsumer(_shippingQueue);
            List<String> expectedShippingOrders = new ArrayList<String>(Arrays.asList("BREAD", "JAM", "MILK", "JAM"));
            Message msg = null;
            while ((msg = consumer.receive(1000)) != null) {
                Assert.assertTrue(expectedShippingOrders.remove(TextMessage.class.cast(msg).getText()));
            }
            Assert.assertEquals(0, expectedShippingOrders.size());
            consumer.close();

            consumer = session.createConsumer(_fillingStockQueue);
            List<String> expectedFillingStockOrders = new ArrayList<String>(Arrays.asList("PIZZA", "POTATO"));
            while ((msg = consumer.receive(1000)) != null) {
                Assert.assertTrue(expectedFillingStockOrders.remove(TextMessage.class.cast(msg).getText()));
            }
            Assert.assertEquals(0, expectedFillingStockOrders.size());
            consumer.close();
        } finally {
            conn.close();
        }
    }
}
