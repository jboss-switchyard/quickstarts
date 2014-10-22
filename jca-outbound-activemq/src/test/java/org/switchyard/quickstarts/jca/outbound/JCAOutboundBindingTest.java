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

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

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
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.testutil.JBossCliUtil;

/**
 * Functional test for a switchyard-quickstart-jca-outbound-activemq.
 */
@RunWith(Arquillian.class)
public class JCAOutboundBindingTest {
    private static final Logger _logger = Logger.getLogger(JCAOutboundBindingTest.class);
    private static final String CLI_CONFIG_FILE = System.getProperty("cli.config.file");
    private static final String CLI_UNCONFIG_FILE = System.getProperty("cli.unconfig.file");
    private static final String QUICKSTART_JAR = System.getProperty("quickstart.jar");
    private static final String ORDER_QUEUE = "OrderQueue";
    private static final String SHIPPING_QUEUE = "ShippingQueue";
    private static final String FILLING_STOCK_QUEUE = "FillingStockQueue";

    private static final File ACTIVEMQ_DATA_DIR = new File(System.getProperty("java.io.tmpdir"), "activemq-data");
    private static final long MEMORY_STORE_USAGE_LIMIT = 209715200;
    private static final long TEMP_STORE_USAGE_LIMIT = 209715200;

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

    @Deployment(order=1, name="activemq-ra.rar")
    public static ResourceAdapterArchive createActiveMQRAR() throws Exception {
        startActiveMQBroker();
        File rar =  Maven.resolver()
                         .loadPomFromFile("./pom.xml")
                         .resolve("org.apache.activemq:activemq-rar:rar:?")
                         .withoutTransitivity()
                         .asSingleFile();
        return ShrinkWrap.create(ZipImporter.class, "activemq-ra.rar")
                         .importFrom(new ZipFile(rar))
                         .as(ResourceAdapterArchive.class);
    }

    @Deployment(order=2)
    public static Archive<?> createTestArchive() throws Exception {
        File artifact = new File(QUICKSTART_JAR);
        if (!artifact.exists()) {
            String error = QUICKSTART_JAR + " not found. Do \"mvn package\" before the test";
            _logger.error(error);
            throw new RuntimeException(error);
        }
        try {
            JBossCliUtil.executeCliScript(CLI_CONFIG_FILE);
            return ShrinkWrap.create(ZipImporter.class, artifact.getName())
                .importFrom(new ZipFile(artifact))
                .as(JavaArchive.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        JBossCliUtil.executeCliScript(CLI_UNCONFIG_FILE);
    }

    /**
     * Triggers the 'OrderService' by sending a ActiveMQ Message to the 'OrderQueue'
     */
    @Test
    public void testOrderService() throws Exception {
        String[] orders = { "BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM" };
        ActiveMQConnectionFactory connectionFactory =
            new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection(
                ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(session.createQueue(ORDER_QUEUE));
            for (String order : orders) {
                TextMessage message = session.createTextMessage();
                message.setText(order);
                producer.send(message);
            }
            session.close();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(session.createQueue(ORDER_QUEUE));
            Assert.assertNull("Request message is still in the queue: " + ORDER_QUEUE, consumer.receive(3000));
            consumer.close();

            consumer = session.createConsumer(session.createQueue(SHIPPING_QUEUE));
            List<String> expectedShippingOrders = new ArrayList<String>(Arrays.asList("BREAD", "JAM", "MILK", "JAM"));
            Message msg = null;
            while ((msg = consumer.receive(1000)) != null) {
                if (msg instanceof TextMessage) {
                    Assert.assertTrue(expectedShippingOrders.remove(((TextMessage) msg).getText()));
                }
            }
            Assert.assertEquals(0, expectedShippingOrders.size());
            consumer.close();

            consumer = session.createConsumer(session.createQueue(FILLING_STOCK_QUEUE));
            List<String> expectedFillingStockOrders = new ArrayList<String>(Arrays.asList("PIZZA", "POTATO"));
            while ((msg = consumer.receive(1000)) != null) {
                if (msg instanceof TextMessage) {
                    Assert.assertTrue(expectedFillingStockOrders.remove(((TextMessage) msg).getText()));
                }
            }
            Assert.assertEquals(0, expectedFillingStockOrders.size());
            session.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
