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
package org.switchyard.quickstarts.camel.jms.binding;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * JMS client that uses HornetQ/ActiveMQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Daniel Bevenius
 *
 */
public final class JMSClient {

    /**
     * The queue to send to.
     */
    private static final String QUEUE_NAME = "GreetingServiceQueue";

    /**
     * The name of the file containing the message content.
     */
    private static final String MESSAGE_PAYLOAD = "test.txt";

    private static final String HQ_USER = "guest";
    private static final String HQ_PASSWD = "guestp.1";

    private static final String AMQ_USER = "karaf";
    private static final String AMQ_PASSWD = "karaf";
    private static final String AMQ_BROKER_URL = "tcp://localhost:61616";

    /**
     * Private no-args constructor.
     */
    private JMSClient() {
    }

    /**
     * Only execution point for this application.
     * @param args command line args.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("activemq")) {
            sendToActiveMQ();
        } else {
            sendToHornetQ();
        }
    }

    private static void sendToHornetQ() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(HQ_USER)
                                    .setPassword(HQ_PASSWD);
        hqMixIn.initialize();
        Session session = null;
        try {
            session = hqMixIn.createJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_NAME));
            Message message = hqMixIn.createJMSMessageFromResource(MESSAGE_PAYLOAD);
            producer.send(message);
            System.out.println("Message sent. Please see server console output");
        } finally {
            hqMixIn.uninitialize();
        }
    }

    private static void sendToActiveMQ() throws Exception {
        ConnectionFactory cf = new ActiveMQConnectionFactory(AMQ_USER, AMQ_PASSWD, AMQ_BROKER_URL);
        Connection conn = cf.createConnection();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));
            BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(MESSAGE_PAYLOAD)));
            StringBuilder buf = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            reader.close();
            Message message = session.createTextMessage(buf.toString());
            producer.send(message);
            System.out.println("Message sent. Please see server console output");
        } finally {
            conn.close();
        }
    }
}
