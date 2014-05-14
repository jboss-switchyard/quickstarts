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
package org.switchyard.quickstarts.bpel.service.hello;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

public final class JMSClient {

    /**
     * The queue to send to.
     */
    private static final String REQUEST_NAME = "HelloRequestQueue";
    private static final String REPLY_NAME = "HelloReplyQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp.1";

    private static final String AMQ_USER = "karaf";
    private static final String AMQ_PASSWD = "karaf";
    private static final String AMQ_BROKER_URL = "tcp://localhost:61616";

    private JMSClient() {
    }

    public static void main(final String[] args) throws Exception {

        if (args.length > 1 && args[0].equalsIgnoreCase("activemq")) {
            sendToActiveMQ(args[1]);
        } else if (args.length != 1) {
            System.err.println("ERROR: Use -Dexec.args to pass a name value, e.g. -Dexec.args=\"Skippy\", or for ActiveMQ, -Dexec.args=\"activemq Skippy\"");
            return;
        } else {
            sendToHornetQ(args[0]);
        }
    }
    
    private static void sendToHornetQ(String value) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
            .setUser(USER)
            .setPassword(PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(REQUEST_NAME));
            final MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(REPLY_NAME));

            producer.send(hqMixIn.createJMSMessage(createPayload(value)));
            System.out.println("Message sent. Waiting for reply ...");

            Message message = consumer.receive(3000);
            String reply = hqMixIn.readStringFromJMSMessage(message);
            System.out.println("REPLY: \n" + reply);
        } finally {
            hqMixIn.uninitialize();
        }
    }

    private static void sendToActiveMQ(String value) throws Exception {
        ConnectionFactory cf = new ActiveMQConnectionFactory(AMQ_USER, AMQ_PASSWD, AMQ_BROKER_URL);
        Connection conn = cf.createConnection();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer producer = session.createProducer(session.createQueue(REQUEST_NAME));
            final MessageConsumer consumer = session.createConsumer(session.createQueue(REPLY_NAME));
            conn.start();
            
            producer.send(session.createTextMessage(createPayload(value)));
            System.out.println("Message sent. Waiting for reply ...");


            Message message = consumer.receive(3000);
            String reply = ((TextMessage) message).getText();
            System.out.println("REPLY: \n" + reply);
        } finally {
            conn.close();
        }
    }

    private static String createPayload(String name) {
        return "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
            + "<exam:input>" + name + "</exam:input>"
            + "</exam:sayHello>";
    }
}
