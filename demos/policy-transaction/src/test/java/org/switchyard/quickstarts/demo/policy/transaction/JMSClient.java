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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * HornetQ client that uses HornetQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Daniel Bevenius
 *
 */
public final class JMSClient {

    private static final String HQ_USER = "guest";
    private static final String HQ_PASSWD = "guestp.1";

    private static final String AMQ_USER = "karaf";
    private static final String AMQ_PASSWD = "karaf";
    private static final String AMQ_BROKER_URL = "tcp://localhost:61616";

    private static final String DEFAULT_QUEUE_NAME = "policyQSTransacted";
    private static final String QUEUE_A = "queueA";
    private static final String QUEUE_B = "queueB";
    private static final String QUEUE_C = "queueC";
    private static final String DEFAULT_PAYLOAD = "rollback.A";

    /**
     * Private no-args constructor.
     */
    private JMSClient() {
    }

    /**
     * Only execution point for this application.
     * @param args parameters
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        String payload = DEFAULT_PAYLOAD;
        String queueName = DEFAULT_QUEUE_NAME;
        String provider = "HornetQ";

        if (args.length == 0) {
            System.out.println("Using queue name: " + queueName);
            System.out.println("Using payload: " + payload);
            sendToHornetQ(payload, queueName);
        } else if (args[0].equalsIgnoreCase("activemq")) {
            provider = "ActiveMQ";
            if (args.length == 2) {
                payload = args[1];
            } else if (args.length == 3) {
                payload = args[1];
                queueName = args[2];
            } else if (args.length > 4){
                throw new Exception("Too many arguments");
            }
            System.out.println("Using queue name: " + queueName);
            System.out.println("Using payload: " + payload);
            sendToActiveMQ(payload, queueName);
        } else {
            if (args.length == 1) {
                payload = args[0];
            } else if (args.length == 2) {
                payload = args[0];
                queueName = args[1];
            } else {
                throw new Exception("Too many arguments");
            }
            System.out.println("Using queue name: " + queueName);
            System.out.println("Using payload: " + payload);
            sendToHornetQ(payload, queueName);
        }
        System.out.println("Message sent: provider=" + provider + ", queueName=" + queueName + ", payload=" + payload);
    }

    private static void sendToHornetQ(String payload, String queueName) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
            .setUser(HQ_USER)
            .setPassword(HQ_PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(queueName));
            producer.send(hqMixIn.createJMSMessage(payload));
            session.close();
            verifyOutputQueue(hqMixIn.createJMSSession());
        } finally {
            hqMixIn.uninitialize();
        }
    }

    private static void sendToActiveMQ(String payload, String queueName) throws Exception {
        ConnectionFactory cf = new ActiveMQConnectionFactory(AMQ_USER, AMQ_PASSWD, AMQ_BROKER_URL);
        Connection conn = cf.createConnection();
        conn.start();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue(queueName));
            producer.send(session.createTextMessage(payload));
            session.close();
            verifyOutputQueue(conn.createSession(false, Session.AUTO_ACKNOWLEDGE));
        } finally {
            conn.close();
        }
    }

    private static void verifyOutputQueue(Session session) throws Exception {
        MessageConsumer consumer = session.createConsumer(session.createQueue(QUEUE_A));
        int countA = 0;
        while ((consumer.receive(30000)) != null) {
            countA++;
        }
        consumer = session.createConsumer(session.createQueue(QUEUE_B));
        int countB = 0;
        while ((consumer.receive(1000)) != null) {
            countB++;
        }
        consumer = session.createConsumer(session.createQueue(QUEUE_C));
        int countC = 0;
        while ((consumer.receive(1000)) != null) {
            countC++;
        }
        System.out.println(QUEUE_A + "=" + countA + ", " + QUEUE_B + "=" + countB + ", " + QUEUE_C + "=" + countC);
    }
}
