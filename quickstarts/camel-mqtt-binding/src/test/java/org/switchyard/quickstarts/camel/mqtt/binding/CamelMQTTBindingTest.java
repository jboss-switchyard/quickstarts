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
package org.switchyard.quickstarts.camel.mqtt.binding;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/**
 * CamelMQTTBindingTest.
 */
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = CDIMixIn.class)
    @RunWith(SwitchYardRunner.class)
public class CamelMQTTBindingTest {

    private static final String TOPIC_INPUT = "camel/mqtt/test/input";
    private static final String TOPIC_OUTPUT = "camel/mqtt/test/output";
    private static final String MESSAGE_INPUT = "Captain Crunch";
    private static final String MESSAGE_OUTPUT = "Hello there " + MESSAGE_INPUT + " :-) ";
    private static BrokerService _broker;

    private SwitchYardTestKit _testKit;
    @ServiceOperation("GreetingService.greet")
    private Invoker _greet;

    @BeforeDeploy
    public void initialize() throws Exception {
        if (_broker != null && _broker.isStarted()) {
            _broker.stop();
        }
        _broker = new BrokerService();
        _broker.setBrokerName("default");
        _broker.setUseJmx(false);
        _broker.setPersistent(false);
        _broker.addConnector("mqtt://localhost:1883");
        _broker.setUseShutdownHook(false);
        _broker.start();
    }

    @Before
    public void before() {
        MQTT mqtt = new MQTT();
        Topic outputTopic = new Topic(TOPIC_OUTPUT, QoS.AT_LEAST_ONCE);
        BlockingConnection connection = mqtt.blockingConnection();
        try {
            connection.connect();
            connection.subscribe(new Topic[]{outputTopic});
            while(connection.receive(1000, TimeUnit.MILLISECONDS) != null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Ignore("Disable for now due to https://issues.jboss.org/browse/SWITCHYARD-2221")
    @Test
    public void testReferenceBinding() throws Exception {
        MQTT mqtt = new MQTT();
        Topic outputTopic = new Topic(TOPIC_OUTPUT, QoS.AT_LEAST_ONCE);
        BlockingConnection connection = mqtt.blockingConnection();
        try {
            connection.connect();
            connection.subscribe(new Topic[]{outputTopic});

            _greet.sendInOnly(MESSAGE_INPUT);
            Message message = connection.receive(1000, TimeUnit.MILLISECONDS);
            Assert.assertNotNull("No output message from " + TOPIC_OUTPUT, message);
            Assert.assertEquals(MESSAGE_OUTPUT, new String(message.getPayload()));
            Assert.assertNull("More than one message received from " + TOPIC_OUTPUT,
                    connection.receive(1000, TimeUnit.MILLISECONDS));
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void testServiceBinding() throws Exception {
        MockHandler mock = _testKit.replaceService("StoreReference");
        MQTT mqtt = new MQTT();
        BlockingConnection connection = mqtt.blockingConnection();
       try {
           connection.connect();
           connection.publish(TOPIC_INPUT, MESSAGE_INPUT.getBytes(), QoS.AT_LEAST_ONCE, false);
           Thread.sleep(1000);

           LinkedBlockingQueue<Exchange> received = mock.getMessages();
           Assert.assertNotNull(received);
           Exchange exchange = received.iterator().next();
           Assert.assertEquals(MESSAGE_OUTPUT, exchange.getMessage().getContent(String.class));
       } finally {
           connection.disconnect();
       }
    }
}
