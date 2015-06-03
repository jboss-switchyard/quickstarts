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
package org.switchyard.quickstarts.eardeploy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.milyn.io.StreamUtils;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * JMS-based client for submitting orders to the OrderService.
 */
public final class OrderIntakeClient {

    private static final String ORDER_QUEUE_NAME = "EAROrderRequestQueue";
    private static final String ORDERACK_QUEUE_NAME = "EAROrderReplyQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp.1";

    /**
     * Main routing for OrderIntakeClient
     * @param args command-line args
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
            .setUser(USER)
            .setPassword(PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(ORDER_QUEUE_NAME));

            String orderTxt = readFileContent(args[0]);
            System.out.println("Submitting Order" + "\n"
                + "----------------------------\n"
                + orderTxt
                + "\n----------------------------");
            producer.send(hqMixIn.createJMSMessage(orderTxt));
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(ORDERACK_QUEUE_NAME));
            System.out.println("Order submitted ... waiting for reply.");
            TextMessage reply = (TextMessage) consumer.receive(3000);
            if (reply == null) {
                System.out.println("No reply received.");
            } else {
                System.out.println("Received reply" + "\n"
                    + "----------------------------\n"
                    + reply.getText()
                    + "\n----------------------------");
            }
        } finally {
            hqMixIn.uninitialize();
        }
    }

    /**
     * Reads the contends of the {@link #MESSAGE_PAYLOAD} file.
     *
     * @param fileName the name of the file to read.
     * @return String the contents of {@link #MESSAGE_PAYLOAD} file.
     * @throws Exception if an exceptions occurs while reading the file.
     */
    public static String readFileContent(final String fileName) throws Exception {
        InputStream in = null;
        try {
            in = new FileInputStream(fileName);
            return StreamUtils.readStreamAsString(in);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
