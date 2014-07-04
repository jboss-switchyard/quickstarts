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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 * MQTT client that uses ActiveMQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Douglas Palmer
 *
 */
public final class MQTTClient {

    private static final String TOPIC_INPUT = "camel/mqtt/test/input";
    private static final String TOPIC_OUTPUT = "camel/mqtt/test/output";
    private static final String MESSAGE_PAYLOAD = "test.txt";
    private static final String USER_NAME = "karaf";
    private static final String PASSWORD = "karaf"; 

    /**
     * Only execution point for this application.
     * @param args command line args.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(MESSAGE_PAYLOAD)));
        String payload = reader.readLine();
        reader.close();

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
            publishConnection.publish(TOPIC_INPUT, payload.getBytes(), QoS.AT_LEAST_ONCE, false);
            System.out.println("Published a message to " + TOPIC_INPUT + ": " + payload);

            Message msg = null;
            while((msg = subscribeConnection.receive(3000, TimeUnit.MILLISECONDS)) != null) {
                System.out.println("Received a message from " + TOPIC_OUTPUT + ": " + new String(msg.getPayload()));
            }
        } finally {
            if (publishConnection != null && publishConnection.isConnected()) {
                publishConnection.disconnect();
            }
            if (subscribeConnection != null && subscribeConnection.isConnected()) {
                subscribeConnection.disconnect();
            }
        }
    }
}
