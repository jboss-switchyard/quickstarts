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

import javax.jms.MessageProducer;
import javax.jms.Session;

import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * HornetQ client that uses HornetQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Daniel Bevenius
 *
 */
public final class HornetQClient {

    /**
     * The queue to send to.
     */
    private static final String DEFAULT_QUEUE_NAME = "policyQSTransacted";

    /**
     * The user name to connect to JMS server.
     */
    private static final String USER = "guest";

    /**
     * The password to connect to JMS server.
     */
    private static final String PASSWD = "guestp.1";

    /**
     * The name of the file containing the message content.
     */
    private static final String DEFAULT_PAYLOAD = "rollback.A";

    /**
     * Private no-args constructor.
     */
    private HornetQClient() {
    }

    /**
     * Only execution point for this application.
     * @param args parameters
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
            .setUser(USER)
            .setPassword(PASSWD);
        hqMixIn.initialize();

        // Parse arguments - expecting : [payload] [queueName]
        String queueName = DEFAULT_QUEUE_NAME;
        String payload = DEFAULT_PAYLOAD;

        if (args.length > 0) {
            payload = args[0];
        }
        if (args.length > 1) {
            queueName = args[1];
        }
        System.out.println("Using queue name: " + queueName);
        System.out.println("Using payload: " + payload);

        try {
            Session session = hqMixIn.getJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(queueName));
            producer.send(hqMixIn.createJMSMessage(payload));
            System.out.println("Message sent. Please see server console output");
        } finally {
            hqMixIn.uninitialize();
        }
    }
}
