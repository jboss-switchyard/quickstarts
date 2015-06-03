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
package org.switchyard.test.quickstarts;

import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class CamelMQTTBindingQuickstartTest {

    private static final String TOPIC_INPUT = "camel/mqtt/test/input";
    private static final String TOPIC_OUTPUT = "camel/mqtt/test/output";
    private static final String MESSAGE_INPUT = "SpongeBob";
    private static final String MESSAGE_OUTPUT = "Hello there " + MESSAGE_INPUT + " :-) ";
    private static final String USER_NAME = "karaf";
    private static final String PASSWORD = "karaf"; 

    private static BrokerService _broker;

    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws Exception {
        _broker = new BrokerService();
        _broker.setBrokerName("default");
        _broker.setUseJmx(false);
        _broker.setPersistent(false);
        _broker.addConnector("mqtt://localhost:1883");
        _broker.setUseShutdownHook(false);
        _broker.start();

        return ArquillianUtil.createJarQSDeployment("switchyard-camel-mqtt-binding");
    }

    @Test
    public void testDeployment() throws Exception {
        BlockingConnection publishConnection = null;
        BlockingConnection subscribeConnection = null;
        try {
            Topic outputTopic = new Topic(TOPIC_OUTPUT, QoS.AT_LEAST_ONCE);
            MQTT mqtt = new MQTT();
            mqtt.setUserName(USER_NAME);
            mqtt.setPassword(PASSWORD);
            subscribeConnection = mqtt.blockingConnection();
            subscribeConnection.connect();
            subscribeConnection.subscribe(new Topic[]{outputTopic});

            publishConnection = mqtt.blockingConnection();
            publishConnection.connect();
            publishConnection.publish(TOPIC_INPUT, MESSAGE_INPUT.getBytes(), QoS.AT_LEAST_ONCE, false);

            /* Disable for now due to https://issues.jboss.org/browse/SWITCHYARD-2221
             * 
            Message message = subscribeConnection.receive(1000, TimeUnit.MILLISECONDS);
            Assert.assertNotNull("No output message from " + TOPIC_OUTPUT, message);
            Assert.assertEquals(MESSAGE_OUTPUT, new String(message.getPayload()));
            Assert.assertNull("More than one message received from " + TOPIC_OUTPUT,
                    subscribeConnection.receive(1000, TimeUnit.MILLISECONDS));
             */
        } finally {
            if (publishConnection != null && publishConnection.isConnected()) {
                publishConnection.disconnect();
            }
            if (subscribeConnection != null && subscribeConnection.isConnected()) {
                subscribeConnection.disconnect();
            }
        }
    }

    @AfterClass
    public static void cleanup() throws Exception {
        if (_broker != null) {
            _broker.stop();
        }
    }
}
